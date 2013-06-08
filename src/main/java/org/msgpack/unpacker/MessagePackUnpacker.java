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
package org.msgpack.unpacker;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.io.BufferReferer;
import org.msgpack.io.Input;
import org.msgpack.io.StreamInput;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.ValueType;

public class MessagePackUnpacker extends AbstractUnpacker {
    private static final byte REQUIRE_TO_READ_HEAD = (byte) 0xc6;

    protected final Input in;
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
    private final ValueAccept valueAccept = new ValueAccept();
    private final SkipAccept skipAccept = new SkipAccept();

    public MessagePackUnpacker(final MessagePack msgpack,
            final InputStream stream) {
        this(msgpack, new StreamInput(stream));
    }

    protected MessagePackUnpacker(final MessagePack msgpack, final Input in) {
        super(msgpack);
        this.in = in;
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
            if (!tryReferRawBody(a, count)) {
                readRawBody(count);
                a.acceptRaw(raw);
                raw = null;
            }
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
            a.acceptFloat(in.getFloat());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcb: // double
            a.acceptDouble(in.getDouble());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcc: // unsigned int 8
            a.acceptUnsignedInteger(in.getByte());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcd: // unsigned int 16
            a.acceptUnsignedInteger(in.getShort());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xce: // unsigned int 32
            a.acceptUnsignedInteger(in.getInt());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xcf: // unsigned int 64
            a.acceptUnsignedInteger(in.getLong());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd0: // signed int 8
            a.acceptInteger(in.getByte());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd1: // signed int 16
            a.acceptInteger(in.getShort());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd2: // signed int 32
            a.acceptInteger(in.getInt());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xd3: // signed int 64
            a.acceptInteger(in.getLong());
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        case 0xda: // raw 16
        {
            final int count = in.getShort() & 0xffff;
            if (count == 0) {
                a.acceptEmptyRaw();
                in.advance();
                headByte = REQUIRE_TO_READ_HEAD;
                return true;
            }
            if (count >= rawSizeLimit) {
                final String reason = String.format(
                        "Size of raw (%d) over limit at %d", new Object[] {
                                count, rawSizeLimit });
                throw new SizeLimitException(reason);
            }
            in.advance();
            if (!tryReferRawBody(a, count)) {
                readRawBody(count);
                a.acceptRaw(raw);
                raw = null;
            }
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        case 0xdb: // raw 32
        {
            final int count = in.getInt();
            if (count == 0) {
                a.acceptEmptyRaw();
                in.advance();
                headByte = REQUIRE_TO_READ_HEAD;
                return true;
            }
            if (count < 0 || count >= rawSizeLimit) {
                final String reason = String.format(
                        "Size of raw (%d) over limit at %d", new Object[] {
                                count, rawSizeLimit });
                throw new SizeLimitException(reason);
            }
            in.advance();
            if (!tryReferRawBody(a, count)) {
                readRawBody(count);
                a.acceptRaw(raw);
                raw = null;
            }
            headByte = REQUIRE_TO_READ_HEAD;
            return true;
        }
        case 0xdc: // array 16
        {
            final int count = in.getShort() & 0xffff;
            if (count >= arraySizeLimit) {
                final String reason = String.format(
                        "Size of array (%d) over limit at %d", new Object[] {
                                count, arraySizeLimit });
                throw new SizeLimitException(reason);
            }
            a.acceptArray(count);
            stack.reduceCount();
            stack.pushArray(count);
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xdd: // array 32
        {
            final int count = in.getInt();
            if (count < 0 || count >= arraySizeLimit) {
                final String reason = String.format(
                        "Size of array (%d) over limit at %d", new Object[] {
                                count, arraySizeLimit });
                throw new SizeLimitException(reason);
            }
            a.acceptArray(count);
            stack.reduceCount();
            stack.pushArray(count);
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xde: // map 16
        {
            final int count = in.getShort() & 0xffff;
            if (count >= mapSizeLimit) {
                final String reason = String.format(
                        "Size of map (%d) over limit at %d", new Object[] {
                                count, mapSizeLimit });
                throw new SizeLimitException(reason);
            }
            a.acceptMap(count);
            stack.reduceCount();
            stack.pushMap(count);
            in.advance();
            headByte = REQUIRE_TO_READ_HEAD;
            return false;
        }
        case 0xdf: // map 32
        {
            final int count = in.getInt();
            if (count < 0 || count >= mapSizeLimit) {
                final String reason = String.format(
                        "Size of map (%d) over limit at %d", new Object[] {
                                count, mapSizeLimit });
                throw new SizeLimitException(reason);
            }
            a.acceptMap(count);
            stack.reduceCount();
            stack.pushMap(count);
            in.advance();
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

    private boolean tryReferRawBody(final BufferReferer referer, final int size)
            throws IOException {
        return in.tryRefer(referer, size);
    }

    private void readRawBody(final int size) throws IOException {
        raw = new byte[size];
        rawFilled = 0;
        readRawBodyCont();
    }

    private void readRawBodyCont() throws IOException {
        final int len = in.read(raw, rawFilled, raw.length - rawFilled);
        rawFilled += len;
        if (rawFilled < raw.length) {
            throw new EOFException();
        }
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
        throw new MessageTypeException("Expected nil but got not nil value");
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
        throw new MessageTypeException(
                "Expected Boolean but got not boolean value");
    }

    @Override
    public byte readByte() throws IOException {
        // optimized not to allocate byteAccept
        stack.checkCount();
        readOneWithoutStack(intAccept);
        final int value = intAccept.value;
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new MessageTypeException(); // TODO message
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
            throw new MessageTypeException(); // TODO message
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
            throw new MessageTypeException(); // TODO message
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
    public String readString() throws IOException {
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
            throw new MessageTypeException(
                    "readArrayEnd() is called but readArrayBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
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
            throw new MessageTypeException(
                    "readMapEnd() is called but readMapBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
                        "readMapEnd(check=true) is called but the map is not end");
            }
            for (int i = 0; i < remain; i++) {
                skip();
            }
        }
        stack.pop();
    }

    @Override
    protected void readValue(final Unconverter uc) throws IOException {
        if (uc.getResult() != null) {
            uc.resetResult();
        }
        valueAccept.setUnconverter(uc);

        stack.checkCount();
        if (readOneWithoutStack(valueAccept)) {
            stack.reduceCount();
            if (uc.getResult() != null) {
                return;
            }
        }
        while (true) {
            while (stack.getTopCount() == 0) {
                if (stack.topIsArray()) {
                    uc.writeArrayEnd(true);
                    stack.pop();
                    // stack.reduceCount();
                } else if (stack.topIsMap()) {
                    uc.writeMapEnd(true);
                    stack.pop();
                    // stack.reduceCount();
                } else {
                    throw new RuntimeException("invalid stack"); // FIXME error?
                }
                if (uc.getResult() != null) {
                    return;
                }
            }
            readOne(valueAccept);
        }
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
        in.close();
    }

    @Override
    public int getReadByteCount() {
        return in.getReadByteCount();
    }

    @Override
    public void resetReadByteCount() {
        in.resetReadByteCount();
    }
}
