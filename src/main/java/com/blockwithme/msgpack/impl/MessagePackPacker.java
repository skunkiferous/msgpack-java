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
import java.io.DataOutput;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * The MessagePack Packer reuses the code form the original Java implementation
 * of the MessagePack Packer.
 *
 * It is limited to the core protocol implementation, without support for Java
 * objects.
 *
 * @author monster
 */
public class MessagePackPacker extends AbstractPacker {
    protected final DataOutput out;
    protected final OutputStream outputStream;

    /** Amounts of raw bytes still to be written. */
    private int rawToWrite;

    /** True if we are in a raw write. */
    private boolean inRawWrite;

    private final PackerStack stack = new PackerStack();

    public MessagePackPacker(final DataOutput out) {
        this.out = Objects.requireNonNull(out);
        if (out instanceof OutputStream) {
            outputStream = (OutputStream) out;
        } else {
            outputStream = new DataOutputWrapperStream(out);
        }
    }

    @Override
    public void writeByte(final byte d) throws IOException {
        if (d < -(1 << 5)) {
            out.writeByte((byte) 0xd0);
            out.writeByte(d);
        } else {
            out.writeByte(d);
        }
        stack.reduceCount();
    }

    @Override
    public void writeShort(final short d) throws IOException {
        if (d < -(1 << 5)) {
            if (d < -(1 << 7)) {
                // signed 16
                out.writeByte((byte) 0xd1);
                out.writeShort(d);
            } else {
                // signed 8
                out.writeByte((byte) 0xd0);
                out.writeByte((byte) d);
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1 << 8)) {
                // unsigned 8
                out.writeByte((byte) 0xcc);
                out.writeByte((byte) d);
            } else {
                // unsigned 16
                out.writeByte((byte) 0xcd);
                out.writeShort(d);
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeChar(final char d) throws IOException {
        if (d < -(1 << 5)) {
            if (d < -(1 << 7)) {
                // signed 16
                out.writeByte((byte) 0xd1);
                out.writeShort((short) d);
            } else {
                // signed 8
                out.writeByte((byte) 0xd0);
                out.writeByte((byte) d);
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1 << 8)) {
                // unsigned 8
                out.writeByte((byte) 0xcc);
                out.writeByte((byte) d);
            } else {
                // unsigned 16
                out.writeByte((byte) 0xcd);
                out.writeShort((short) d);
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeInt(final int d) throws IOException {
        if (d < -(1 << 5)) {
            if (d < -(1 << 15)) {
                // signed 32
                out.writeByte((byte) 0xd2);
                out.writeInt(d);
            } else if (d < -(1 << 7)) {
                // signed 16
                out.writeByte((byte) 0xd1);
                out.writeShort((short) d);
            } else {
                // signed 8
                out.writeByte((byte) 0xd0);
                out.writeByte((byte) d);
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1 << 8)) {
                // unsigned 8
                out.writeByte((byte) 0xcc);
                out.writeByte((byte) d);
            } else if (d < (1 << 16)) {
                // unsigned 16
                out.writeByte((byte) 0xcd);
                out.writeShort((short) d);
            } else {
                // unsigned 32
                out.writeByte(d);
                out.writeInt(d);
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeLong(final long d) throws IOException {
        if (d < -(1L << 5)) {
            if (d < -(1L << 15)) {
                if (d < -(1L << 31)) {
                    // signed 64
                    out.writeByte((byte) 0xd3);
                    out.writeLong(d);
                } else {
                    // signed 32
                    out.writeByte((byte) 0xd2);
                    out.writeInt((int) d);
                }
            } else {
                if (d < -(1 << 7)) {
                    // signed 16
                    out.writeByte((byte) 0xd1);
                    out.writeShort((short) d);
                } else {
                    // signed 8
                    out.writeByte((byte) 0xd0);
                    out.writeByte((byte) d);
                }
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1L << 16)) {
                if (d < (1 << 8)) {
                    // unsigned 8
                    out.writeByte((byte) 0xcc);
                    out.writeByte((byte) 0xcc);
                } else {
                    // unsigned 16
                    out.writeByte((byte) 0xcd);
                    out.writeShort((short) d);
                }
            } else {
                if (d < (1L << 32)) {
                    // unsigned 32
                    out.writeByte((byte) 0xce);
                    out.writeInt((int) d);
                } else {
                    // unsigned 64
                    out.writeByte((byte) 0xcf);
                    out.writeLong(d);
                }
            }
        }
        stack.reduceCount();
    }

    @Override
    protected void writeBigInteger(final BigInteger d,
            final boolean countAaValue) throws IOException {
        if (d.bitLength() <= 63) {
            writeLong(d.longValue());
            if (countAaValue) {
                stack.reduceCount();
            }
        } else if (d.bitLength() == 64 && d.signum() == 1) {
            // unsigned 64
            out.writeByte((byte) 0xcf);
            out.writeLong(d.longValue());
            if (countAaValue) {
                stack.reduceCount();
            }
        } else {
            throw new IOException(
                    "MessagePack can't serialize BigInteger larger than (2^64)-1");
        }
    }

    @Override
    public void writeFloat(final float d) throws IOException {
        out.writeByte((byte) 0xca);
        out.writeFloat(d);
        stack.reduceCount();
    }

    @Override
    public void writeDouble(final double d) throws IOException {
        out.writeByte((byte) 0xcb);
        out.writeDouble(d);
        stack.reduceCount();
    }

    @Override
    public void writeBoolean(final boolean d) throws IOException {
        if (d) {
            // true
            out.writeByte((byte) 0xc3);
        } else {
            // false
            out.writeByte((byte) 0xc2);
        }
        stack.reduceCount();
    }

    @Override
    protected void writeByteArray(final byte[] b, final int off, final int len)
            throws IOException {
        writeRawBegin(len);
        out.write(b, off, len);
        rawWritten(len);
        writeRawEnd();
    }

    /**
     * Writes a ByteBuffer *content*, between writeRawBegin(size) and
     * writeRawEnd(). It can be used together with dataOutput(). Note that
     * writePartial(ByteBuffer) calls rawWritten() directly, while you need to
     * do this yourself if using dataOutput().
     */
    @Override
    public void writePartial(final ByteBuffer bb) throws IOException {
        checkInRawWrite();
        final int len = bb.remaining();
        final int pos = bb.position();
        try {
            if (bb.hasArray()) {
                final byte[] array = bb.array();
                final int offset = bb.arrayOffset();
                out.write(array, offset, len);
            } else {
                final byte[] buf = new byte[len];
                bb.get(buf);
                out.write(buf);
            }
            rawWritten(len);
        } finally {
            bb.position(pos);
        }

    }

    @Override
    protected void writeByteBuffer2(final ByteBuffer bb) throws IOException {
        final int len = bb.remaining();
        writeRawBegin(len);
        writePartial(bb);
        writeRawEnd();
    }

    @Override
    protected void writeString(final String s) throws IOException {
        byte[] b;
        try {
            // TODO encoding error?
            b = s.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException ex) {
            throw new IOException(ex);
        }
        writeByteArray(b, 0, b.length);
    }

    @Override
    public void writeNil() throws IOException {
        out.writeByte((byte) 0xc0);
        stack.reduceCount();

    }

    @Override
    public void writeArrayBegin(final int size) throws IOException {
        // TODO check size < 0?
        if (size < 16) {
            // FixArray
            out.writeByte((byte) (0x90 | size));
        } else if (size < 65536) {
            out.writeByte((byte) 0xdc);
            out.writeShort((short) size);
        } else {
            out.writeByte((byte) 0xdd);
            out.writeInt(size);
        }
        stack.reduceCount();
        stack.pushArray(size);

    }

    @Override
    public void writeArrayEnd(final boolean check) throws IOException {
        if (!stack.topIsArray()) {
            throw new IOException(
                    "writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new IOException(
                        "writeArrayEnd(check=true) is called but the array is not end: "
                                + remain);
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

    }

    @Override
    public void writeMapBegin(final int size) throws IOException {
        // TODO check size < 0?
        if (size < 16) {
            // FixMap
            out.writeByte((byte) (0x80 | size));
        } else if (size < 65536) {
            out.writeByte((byte) 0xde);
            out.writeShort((short) size);
        } else {
            out.writeByte((byte) 0xdf);
            out.writeInt(size);
        }
        stack.reduceCount();
        stack.pushMap(size);

    }

    @Override
    public void writeMapEnd(final boolean check) throws IOException {
        if (!stack.topIsMap()) {
            throw new IOException(
                    "writeMapEnd() is called but writeMapBegin() is not called");
        }

        final int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new IOException(
                        "writeMapEnd(check=true) is called but the map is not end: "
                                + remain);
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

    }

    /**
     * Returns the underlying DataOutput: use with extreme care!
     *
     * Before using the underlying DataOutput, you must first call
     * writeRawBegin(size) (which implies you must already know how many bytes
     * you will write. While writing, or after you are done, you must call
     * rawWritten(written), so the Packer knows how much you wrote. When you are
     * done, you must call writeRawEnd(). If the wrong amount of data was
     * written, according to rawWritten(), writeRawEnd() will fail.
     * @throws IOException
     */
    @Override
    public DataOutput dataOutput() throws IOException {
        checkInRawWrite();
        return out;
    }

    /**
     * Returns the underlying OutputStream: use with extreme care!
     *
     * If the underlying DataOutput is an OutputStream, then it will be
     * returned. If not, the a wrapper OutputStream on top of the DataOutput
     * will be written instead.
     *
     * @throws IOException
     */
    @Override
    public OutputStream outputStream() throws IOException {
        checkInRawWrite();
        return outputStream;
    }

    /**
     * Indicate that 'written' bytes were written to the underlying DataOutput.
     * You must call this method, when accessing the underlying DataOutput directly.
     * @throws IOException
     */
    @Override
    public void rawWritten(final int written) throws IOException {
        checkInRawWrite();
        rawToWrite -= written;
    }

    /** Writes an raw begin. */
    @Override
    public void writeRawBegin(final int len) throws IOException {
        if (inRawWrite) {
            throw new IOException("Last raw write not terminated!");
        }
        if (len < 32) {
            out.writeByte((byte) (0xa0 | len));
        } else if (len < 65536) {
            out.writeByte((byte) 0xda);
            out.writeShort((short) len);
        } else {
            out.writeByte((byte) 0xdb);
            out.writeInt(len);
        }
        rawToWrite = len;
        stack.reduceCount();
        stack.pushRaw();
        inRawWrite = true;
    }

    /** Writes an raw end. */
    @Override
    public void writeRawEnd() throws IOException {
        checkInRawWrite();
        // We moved the single raw reduceCOunt() here.
        stack.reduceCount();
        if (!stack.topIsRaw()) {
            throw new IOException(
                    "writeRawEnd() is called but writeRawBegin() is not called");
        }

        if (rawToWrite > 0) {
            throw new IOException("Last raw write not terminated!");
        }
        if (rawToWrite < 0) {
            throw new IOException("Last raw write too big!");
        }
        stack.pop();
        inRawWrite = false;
    }

    public void reset() {
        stack.clear();
    }

    @Override
    public void flush() throws IOException {
        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (out instanceof Closeable) {
            ((Closeable) out).close();
        }
    }

    /** Checks if we are within a raw write, and fails if not. */
    protected void checkInRawWrite() throws IOException {
        if (!inRawWrite) {
            throw new IOException("Not in raw write");
        }
    }
}
