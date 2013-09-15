//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package com.blockwithme.msgpack.impl;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;

import com.blockwithme.msgpack.ValueType;
import com.blockwithme.msgpack.impl.accept.Accept;
import com.blockwithme.msgpack.impl.accept.ArrayAccept;
import com.blockwithme.msgpack.impl.accept.BigIntegerAccept;
import com.blockwithme.msgpack.impl.accept.ByteArrayAccept;
import com.blockwithme.msgpack.impl.accept.DoubleAccept;
import com.blockwithme.msgpack.impl.accept.IntAccept;
import com.blockwithme.msgpack.impl.accept.LongAccept;
import com.blockwithme.msgpack.impl.accept.MapAccept;
import com.blockwithme.msgpack.impl.accept.SkipAccept;
import com.blockwithme.msgpack.impl.accept.StringAccept;
import com.blockwithme.util.DataInputBuffer;

/**
 * The MessagePack Unpacker reuses the code form the original Java implementation
 * of the MessagePack Unpacker.
 *
 * It is limited to the core protocol implementation, without support for Java
 * objects.
 *
 * @author monster
 */
public class MessagePackUnpacker extends AbstractUnpacker {
    private static final byte REQUIRE_TO_READ_HEAD = (byte) 0xc6;

    private final DataInput in;
    private final InputStream inputStream;
    private final UnpackerStack stack = new UnpackerStack();

    private byte headByte = REQUIRE_TO_READ_HEAD;

    private byte[] raw;
    private int rawFilled;

    private final IntAccept intAccept = new IntAccept();
    private final LongAccept longAccept = new LongAccept();
    private final BigIntegerAccept bigIntegerAccept = new BigIntegerAccept();
    private final DoubleAccept doubleAccept = new DoubleAccept();
    private final ByteArrayAccept byteArrayAccept = new ByteArrayAccept();
    private final StringAccept stringAccept = new StringAccept();
    private final ArrayAccept arrayAccept = new ArrayAccept();
    private final MapAccept mapAccept = new MapAccept();
    private final SkipAccept skipAccept = new SkipAccept();

    /** Are we in read raw? */
    private boolean inReadRaw;

    /** Bytes to read in read raw. */
    private int readRawToRead;

    /** Temporary DataInput, used only during raw read. */
    private DataInput tempRawReadDataInput;

    /** Temporary InputStream, used only during raw read. */
    private InputStream tempInputStream;

    public MessagePackUnpacker(final DataInput in) {
        this.in = Objects.requireNonNull(in);
        if (in instanceof InputStream) {
            inputStream = (InputStream) in;
        } else {
            inputStream = new DataInputStreamWrapper(in);
        }
    }

    private byte getHeadByte() throws IOException {
        byte b = headByte;
        if (b == REQUIRE_TO_READ_HEAD) {
            b = headByte = in.readByte();
        }
        return b;
    }

    final void readOne(final Accept a) throws IOException {
        stack.checkCount();
        if (readOneWithoutStack(a)) {
            stack.reduceCount();
        }
    }

    final boolean readOneWithoutStack(final Accept a) throws IOException {
        if (raw != null) {
            readRawBodyCont();
            a.acceptRaw(raw);
            raw = null;
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }

        final int b = getHeadByte();

        if ((b & 0x80) == 0) { // Positive Fixnum
            // System.out.println("positive fixnum "+b);
            a.acceptInteger(b);
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }

        if ((b & 0xe0) == 0xe0) { // Negative Fixnum
            // System.out.println("negative fixnum "+b);
            a.acceptInteger(b);
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }

        if ((b & 0xe0) == 0xa0) { // FixRaw
            final int count = b & 0x1f;
            if (count == 0) {
                a.acceptEmptyRaw();
                headByte = REQUIRE_TO_READ_HEAD;
                return true;
            }
            readRawBody(count);
            a.acceptRaw(raw);
            raw = null;
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }

        if ((b & 0xf0) == 0x90) { // FixArray
            final int count = b & 0x0f;
            // System.out.println("fixarray count:"+count);
            a.acceptArray(count);
            stack.reduceCount();
            stack.pushArray(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }

        if ((b & 0xf0) == 0x80) { // FixMap
            final int count = b & 0x0f;
            // System.out.println("fixmap count:"+count/2);
            a.acceptMap(count);
            stack.reduceCount();
            stack.pushMap(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }

        return readOneWithoutStackLarge(a, b);
    }

    private boolean readOneWithoutStackLarge(final Accept a, final int b)
            throws IOException {
        switch (b & 0xff) {
        case 0xc0: // nil
            a.acceptNil();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xc2: // false
            a.acceptBoolean(false);
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xc3: // true
            a.acceptBoolean(true);
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xca: // float
            a.acceptFloat(in.readFloat());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcb: // double
            a.acceptDouble(in.readDouble());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcc: // unsigned int 8
            a.acceptUnsignedInteger(in.readByte());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcd: // unsigned int 16
            a.acceptUnsignedInteger(in.readShort());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xce: // unsigned int 32
            a.acceptUnsignedInteger(in.readInt());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcf: // unsigned int 64
            a.acceptUnsignedInteger(in.readLong());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd0: // signed int 8
            a.acceptInteger(in.readByte());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd1: // signed int 16
            a.acceptInteger(in.readShort());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd2: // signed int 32
            a.acceptInteger(in.readInt());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd3: // signed int 64
            a.acceptInteger(in.readLong());
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xda: // raw 16
        {
            final int count = in.readShort() & 0xffff;
            if (count == 0) {
                a.acceptEmptyRaw();
                headByte = REQUIRE_TO_READ_HEAD;
                return true;
            }
            if (count >= rawSizeLimit) {
                final String reason = String.format(
                        "Size of raw (%d) over limit at %d", new Object[] {
                                count, rawSizeLimit });
                throw new IOException(reason);
            }
            readRawBody(count);
            a.acceptRaw(raw);
            raw = null;
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        case 0xdb: // raw 32
        {
            final int count = in.readInt();
            if (count == 0) {
                a.acceptEmptyRaw();
                headByte = REQUIRE_TO_READ_HEAD;
                return true;
            }
            if (count < 0 || count >= rawSizeLimit) {
                final String reason = String.format(
                        "Size of raw (%d) over limit at %d", new Object[] {
                                count, rawSizeLimit });
                throw new IOException(reason);
            }
            readRawBody(count);
            a.acceptRaw(raw);
            raw = null;
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        case 0xdc: // array 16
        {
            final int count = in.readShort() & 0xffff;
            if (count >= arraySizeLimit) {
                final String reason = String.format(
                        "Size of array (%d) over limit at %d", new Object[] {
                                count, arraySizeLimit });
                throw new IOException(reason);
            }
            a.acceptArray(count);
            stack.reduceCount();
            stack.pushArray(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xdd: // array 32
        {
            final int count = in.readInt();
            if (count < 0 || count >= arraySizeLimit) {
                final String reason = String.format(
                        "Size of array (%d) over limit at %d", new Object[] {
                                count, arraySizeLimit });
                throw new IOException(reason);
            }
            a.acceptArray(count);
            stack.reduceCount();
            stack.pushArray(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xde: // map 16
        {
            final int count = in.readShort() & 0xffff;
            if (count >= mapSizeLimit) {
                final String reason = String.format(
                        "Size of map (%d) over limit at %d", new Object[] {
                                count, mapSizeLimit });
                throw new IOException(reason);
            }
            a.acceptMap(count);
            stack.reduceCount();
            stack.pushMap(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xdf: // map 32
        {
            final int count = in.readInt();
            if (count < 0 || count >= mapSizeLimit) {
                final String reason = String.format(
                        "Size of map (%d) over limit at %d", new Object[] {
                                count, mapSizeLimit });
                throw new IOException(reason);
            }
            a.acceptMap(count);
            stack.reduceCount();
            stack.pushMap(count);
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        default:
            // System.out.println("unknown b "+(b&0xff));
            // headByte = CS_INVALID
            headByte = REQUIRE_TO_READ_HEAD;
            throw new IOException("Invalid byte: " + b); // TODO error FormatException
        }
    }

    private void readRawBody(final int size) throws IOException {
        raw = new byte[size];
        rawFilled = 0;
        readRawBodyCont();
    }

    private void readRawBodyCont() throws IOException {
        final int len = raw.length - rawFilled;
        in.readFully(raw, rawFilled, len);
        rawFilled += len;
    }

    @Override
    protected boolean tryReadNil() throws IOException {
        stack.checkCount();
        final int b = getHeadByte() & 0xff;
        if (b == 0xc0) {
            // nil is read
            stack.reduceCount();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        // not nil
        return false;
    }

    @Override
    public boolean trySkipNil() throws IOException {
        if (stack.getDepth() > 0 && stack.getTopCount() <= 0) {
            // end of array or map
            return true;
        }

        final int b = getHeadByte() & 0xff;
        if (b == 0xc0) {
            // nil is skipped
            stack.reduceCount();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        // not nil
        return false;
    }

    @Override
    public void readNil() throws IOException {
        // optimized not to allocate nilAccept
        stack.checkCount();
        final int b = getHeadByte() & 0xff;
        if (b == 0xc0) {
            stack.reduceCount();
            headByte = REQUIRE_TO_READ_HEAD;
            return;
        }
        throw new IOException("Expected nil but got not nil value");
    }

    @Override
    public boolean readBoolean() throws IOException {
        // optimized not to allocate booleanAccept
        stack.checkCount();
        final int b = getHeadByte() & 0xff;
        if (b == 0xc2) {
            stack.reduceCount();
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        } else if (b == 0xc3) {
            stack.reduceCount();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        throw new IOException("Expected Boolean but got not boolean value");
    }

    @Override
    public byte readByte() throws IOException {
        // optimized not to allocate byteAccept
        stack.checkCount();
        readOneWithoutStack(intAccept);
        final int value = intAccept.value;
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new IOException(); // TODO message
        }
        stack.reduceCount();
        return (byte) value;
    }

    @Override
    public short readShort() throws IOException {
        // optimized not to allocate shortAccept
        stack.checkCount();
        readOneWithoutStack(intAccept);
        final int value = intAccept.value;
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IOException(); // TODO message
        }
        stack.reduceCount();
        return (short) value;
    }

    @Override
    public char readChar() throws IOException {
        // optimized not to allocate shortAccept
        stack.checkCount();
        readOneWithoutStack(intAccept);
        final int value = intAccept.value;
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new IOException(); // TODO message
        }
        stack.reduceCount();
        return (char) value;
    }

    @Override
    public int readInt() throws IOException {
        readOne(intAccept);
        return intAccept.value;
    }

    @Override
    public long readLong() throws IOException {
        readOne(longAccept);
        return longAccept.value;
    }

    @Override
    public BigInteger readBigInteger() throws IOException {
        readOne(bigIntegerAccept);
        return bigIntegerAccept.value;
    }

    @Override
    protected BigInteger readBigInteger(final boolean countAsValue)
            throws IOException {
        readOne(bigIntegerAccept);
        final BigInteger result = bigIntegerAccept.value;
        if ((result != null) && !countAsValue) {
            stack.raiseCount();
        }
        return result;
    }

    @Override
    public float readFloat() throws IOException {
        readOne(doubleAccept);
        return (float) doubleAccept.value;
    }

    @Override
    public double readDouble() throws IOException {
        readOne(doubleAccept);
        return doubleAccept.value;
    }

    @Override
    public byte[] readByteArray() throws IOException {
        readOne(byteArrayAccept);
        return byteArrayAccept.value;
    }

    @Override
    public String readUTF() throws IOException {
        readOne(stringAccept);
        return stringAccept.value;
    }

    @Override
    public int readArrayBegin() throws IOException {
        readOne(arrayAccept);
        return arrayAccept.size;
    }

    @Override
    public void readArrayEnd(final boolean check) throws IOException {
        if (!stack.topIsArray()) {
            throw new IOException(
                    "readArrayEnd() is called but readArrayBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new IOException(
                        "readArrayEnd(check=true) is called but the array is not end");
            }
            for (int i = 0; i < remain; i++) {
                skip();
            }
        }
        stack.pop();
    }

    @Override
    public int readMapBegin() throws IOException {
        readOne(mapAccept);
        return mapAccept.size;
    }

    @Override
    public void readMapEnd(final boolean check) throws IOException {
        if (!stack.topIsMap()) {
            throw new IOException(
                    "readMapEnd() is called but readMapBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new IOException(
                        "readMapEnd(check=true) is called but the map is not end");
            }
            for (int i = 0; i < remain; i++) {
                skip();
            }
        }
        stack.pop();
    }

    @Override
    public void skip() throws IOException {
        stack.checkCount();
        if (readOneWithoutStack(skipAccept)) {
            stack.reduceCount();
            return;
        }
        final int targetDepth = stack.getDepth() - 1;
        while (true) {
            while (stack.getTopCount() == 0) {
                stack.pop();
                if (stack.getDepth() <= targetDepth) {
                    return;
                }
            }
            readOne(skipAccept);
        }
    }

    @Override
    public ValueType getNextType() throws IOException {
        final int b = getHeadByte();
        if ((b & 0x80) == 0) { // Positive Fixnum
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xe0) { // Negative Fixnum
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xa0) { // FixRaw
            return ValueType.RAW;
        }
        if ((b & 0xf0) == 0x90) { // FixArray
            return ValueType.ARRAY;
        }
        if ((b & 0xf0) == 0x80) { // FixMap
            return ValueType.MAP;
        }
        switch (b & 0xff) {
        case 0xc0: // nil
            return ValueType.NIL;
        case 0xc2: // false
        case 0xc3: // true
            return ValueType.BOOLEAN;
        case 0xca: // float
        case 0xcb: // double
            return ValueType.FLOAT;
        case 0xcc: // unsigned int 8
        case 0xcd: // unsigned int 16
        case 0xce: // unsigned int 32
        case 0xcf: // unsigned int 64
        case 0xd0: // signed int 8
        case 0xd1: // signed int 16
        case 0xd2: // signed int 32
        case 0xd3: // signed int 64
            return ValueType.INTEGER;
        case 0xda: // raw 16
        case 0xdb: // raw 32
            return ValueType.RAW;
        case 0xdc: // array 16
        case 0xdd: // array 32
            return ValueType.ARRAY;
        case 0xde: // map 16
        case 0xdf: // map 32
            return ValueType.MAP;
        default:
            throw new IOException("Invalid byte: " + b); // TODO error FormatException
        }
    }

    public void reset() {
        raw = null;
        stack.clear();
    }

    @Override
    public void close() throws IOException {
        if (in instanceof Closeable) {
            ((Closeable) in).close();
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#dataInput()
     */
    @Override
    public DataInput dataInput() throws IOException {
        checkInRawRead();
        if (inReadRaw) {
            if (tempRawReadDataInput == null) {
                tempRawReadDataInput = new DataInputBuffer(
                        byteArrayAccept.value);
            }
            return tempRawReadDataInput;
        }
        return in;
    }

    /**
     * Returns the underlying InputStream: use with extreme care!
     *
     * If the underlying DataInput is an InputStream, then it will be
     * returned. If not, the a wrapper InputStream on top of the DataInput
     * will be written instead.
     *
     * @see dataInput() for usage restrictions.
     *
     * @throws IOException
     */
    @Override
    public InputStream inputStream() throws IOException {
        if (inReadRaw) {
            if (tempInputStream == null) {
                tempInputStream = new DataInputStreamWrapper(dataInput());
            }
            return tempInputStream;
        }
        checkInRawRead();
        return inputStream;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readRawBegin()
     */
    @Override
    public int readRawBegin() throws IOException {
        if (inReadRaw) {
            throw new IOException("Already in read raw!");
        }
        readOne(byteArrayAccept);
        inReadRaw = true;
        return (readRawToRead = byteArrayAccept.value.length);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readRawEnd()
     */
    @Override
    public void readRawEnd() throws IOException {
        checkInRawRead();
        if (readRawToRead != 0) {
            throw new IOException("Wrong number of bytes read");
        }
        inReadRaw = false;
        tempRawReadDataInput = null;
        tempInputStream = null;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#rawRead(int)
     */
    @Override
    public void rawRead(final int read) throws IOException {
        checkInRawRead();
        readRawToRead -= read;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readPartialByteBuffer(int)
     */
    @Override
    public ByteBuffer readPartialByteBuffer(final int bytes) throws IOException {
        checkInRawRead();
        if ((readRawToRead == bytes)
                && (readRawToRead == byteArrayAccept.value.length)) {
            readRawToRead = 0;
            return ByteBuffer.wrap(byteArrayAccept.value);
        }
        rawRead(bytes);
        final int offset = byteArrayAccept.value.length - readRawToRead;
        return ByteBuffer.wrap(byteArrayAccept.value, offset, bytes);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.impl.AbstractUnpacker#checkInRawRead()
     */
    @Override
    protected void checkInRawRead() throws IOException {
        if (!inReadRaw) {
            throw new IOException("Not in read raw");
        }
    }
}
