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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.blockwithme.msgpack.Packer;

public class MessagePackPacker extends AbstractPacker {
    protected final DataOutput out;

    private int rawToWrite;

    private final PackerStack stack = new PackerStack();

    public MessagePackPacker(final DataOutput out) {
        this.out = out;
    }

    /** Returns the DataOutput. */
    public DataOutput getDataOutput() {
        return out;
    }

    @Override
    protected void writeByte(final byte d) throws IOException {
        if (d < -(1 << 5)) {
            out.writeByte((byte) 0xd0);
            out.writeByte(d);
        } else {
            out.writeByte(d);
        }
        stack.reduceCount();
    }

    @Override
    protected void writeShort(final short d) throws IOException {
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
    protected void writeChar(final char d) throws IOException {
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
    protected void writeInt(final int d) throws IOException {
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
    protected void writeLong(final long d) throws IOException {
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
    protected void writeFloat(final float d) throws IOException {
        out.writeByte((byte) 0xca);
        out.writeFloat(d);
        stack.reduceCount();
    }

    @Override
    protected void writeDouble(final double d) throws IOException {
        out.writeByte((byte) 0xcb);
        out.writeDouble(d);
        stack.reduceCount();
    }

    @Override
    protected void writeBoolean(final boolean d) throws IOException {
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
        rawToWrite -= len;
        writeRawEnd();
    }

    /** Writes a byte array, between a writeRawBegin() and writeRawEnd(). */
    @Override
    public Packer writePartial(final byte[] b) throws IOException {
        return writePartial(b, 0, b.length);
    }

    /** Writes a byte[], between a writeRawBegin() and writeRawEnd(). */
    @Override
    public Packer writePartial(final byte[] b, final int off, final int len)
            throws IOException {
        out.write(b, off, len);
        rawToWrite -= len;
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writePartial(java.nio.ByteBuffer)
     */
    @Override
    public Packer writePartial(final ByteBuffer bb) throws IOException {
        final int len = bb.remaining();
        final int pos = bb.position();
        try {
            if (bb.hasArray()) {
                final byte[] array = bb.array();
                final int offset = bb.arrayOffset();
                out.write(array, offset, len);
                rawToWrite -= len;
            } else {
                final byte[] buf = new byte[len];
                bb.get(buf);
                out.write(buf);
                rawToWrite -= len;
            }
        } finally {
            bb.position(pos);
        }
        return this;
    }

    @Override
    protected void writeByteBuffer(final ByteBuffer bb) throws IOException {
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
    public Packer writeNil() throws IOException {
        out.writeByte((byte) 0xc0);
        stack.reduceCount();
        return this;
    }

    @Override
    public Packer writeArrayBegin(final int size) throws IOException {
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
        return this;
    }

    @Override
    public Packer writeArrayEnd(final boolean check) throws IOException {
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
        return this;
    }

    @Override
    public Packer writeMapBegin(final int size) throws IOException {
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
        return this;
    }

    @Override
    public Packer writeMapEnd(final boolean check) throws IOException {
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
        return this;
    }

    /** Writes an raw begin. */
    @Override
    public Packer writeRawBegin(final int len) throws IOException {
        if (rawToWrite > 0) {
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
        return this;
    }

    /** Writes an raw end. */
    @Override
    public Packer writeRawEnd() throws IOException {
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
        return this;
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
}
