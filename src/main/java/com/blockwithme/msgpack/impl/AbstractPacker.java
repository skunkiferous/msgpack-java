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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import com.blockwithme.msgpack.Packer;

/**
 * The Abstract Packer reuses the code form the original Java implementation
 * of the Abstract Packer.
 *
 * It is limited to the core protocol implementation, without support for Java
 * objects.
 *
 * @author monster
 */
public abstract class AbstractPacker implements Packer {

    /** Deduce from index to optimize storage. */
    public static final int INDEX_OFFSET = 31;

    @Override
    public void writeBoolean(final Boolean o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBoolean(o);
        }

    }

    @Override
    public void writeByte(final Byte o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByte(o);
        }

    }

    @Override
    public void writeShort(final Short o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeShort(o);
        }

    }

    @Override
    public void writeCharacter(final Character o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeChar(o);
        }

    }

    @Override
    public void writeInteger(final Integer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeInt(o);
        }

    }

    @Override
    public void writeLong(final Long o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeLong(o);
        }

    }

    @Override
    public void writeBigInteger(final BigInteger o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o, true);
        }

    }

    @Override
    public void writeBigDecimal(final BigDecimal o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o.unscaledValue(), false);
            writeInt(o.scale());
        }

    }

    @Override
    public void writeFloat(final Float o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeFloat(o);
        }

    }

    @Override
    public void writeDouble(final Double o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeDouble(o);
        }

    }

    /** Writes a Date out. */
    @Override
    public void writeDate(final Date o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeLong(o.getTime());
        }

    }

    @Override
    public void write(final byte[] o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o);
        }

    }

    @Override
    public void write(final byte[] o, final int off, final int len)
            throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o, off, len);
        }

    }

    @Override
    public void writeByteBuffer(final ByteBuffer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteBuffer2(o);
        }

    }

    @Override
    public void writeUTF(final String o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeString(o);
        }

    }

    @Override
    public void writeArrayEnd() throws IOException {
        writeArrayEnd(true);

    }

    @Override
    public void writeMapEnd() throws IOException {
        writeMapEnd(true);

    }

    @Override
    public void writeBooleanArray(final boolean[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final boolean a : target) {
            writeBoolean(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeShortArray(final short[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final short a : target) {
            writeShort(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeCharArray(final char[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final char a : target) {
            writeChar(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeIntArray(final int[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final int a : target) {
            writeInt(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeLongArray(final long[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final long a : target) {
            writeLong(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeFloatArray(final float[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final float a : target) {
            writeFloat(a);
        }
        writeArrayEnd();

    }

    @Override
    public void writeDoubleArray(final double[] target) throws IOException {
        if (target == null) {
            writeNil();

        }
        writeArrayBegin(target.length);
        for (final double a : target) {
            writeDouble(a);
        }
        writeArrayEnd();

    }

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    @Override
    public void writeIndex(final int index) throws IOException {
        writeInt(index - INDEX_OFFSET);

    }

    /** Writes a byte. */
    @Override
    @Deprecated
    public void writeByte(final int o) throws IOException {
        writeByte((byte) o);
    }

    /** Writes a short. */
    @Override
    @Deprecated
    public void writeShort(final int o) throws IOException {
        writeShort((short) o);
    }

    /** Writes a char. */
    @Override
    @Deprecated
    public void writeChar(final int o) throws IOException {
        writeChar((char) o);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeBytes(java.lang.String)
     */
    @Override
    @Deprecated
    public void writeBytes(final String s) throws IOException {
        writeUTF(s);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeChars(java.lang.String)
     */
    @Override
    @Deprecated
    public void writeChars(final String s) throws IOException {
        writeUTF(s);
    }

    /** Writes a *byte*. */
    @Override
    @Deprecated
    public void write(final int o) throws IOException {
        writeByte((byte) o);
    }

    @Override
    public void close() throws IOException {
    }

    abstract protected void writeBigInteger(final BigInteger v,
            final boolean countAaValue) throws IOException;

    protected void writeByteArray(final byte[] b) throws IOException {
        writeByteArray(b, 0, b.length);
    }

    abstract protected void writeByteArray(final byte[] b, final int off,
            final int len) throws IOException;

    abstract protected void writeByteBuffer2(final ByteBuffer bb)
            throws IOException;

    abstract protected void writeString(final String s) throws IOException;
}
