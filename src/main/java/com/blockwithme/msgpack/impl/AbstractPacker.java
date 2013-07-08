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

import com.blockwithme.msgpack.Packer;

public abstract class AbstractPacker implements Packer {

    /** Deduce from index to optimize storage. */
    public static final int INDEX_OFFSET = 31;

    @Override
    public Packer write(final boolean o) throws IOException {
        writeBoolean(o);
        return this;
    }

    @Override
    public Packer write(final byte o) throws IOException {
        writeByte(o);
        return this;
    }

    @Override
    public Packer write(final short o) throws IOException {
        writeShort(o);
        return this;
    }

    @Override
    public Packer write(final char o) throws IOException {
        writeChar(o);
        return this;
    }

    @Override
    public Packer write(final int o) throws IOException {
        writeInt(o);
        return this;
    }

    @Override
    public Packer write(final long o) throws IOException {
        writeLong(o);
        return this;
    }

    @Override
    public Packer write(final float o) throws IOException {
        writeFloat(o);
        return this;
    }

    @Override
    public Packer write(final double o) throws IOException {
        writeDouble(o);
        return this;
    }

    @Override
    public Packer write(final Boolean o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBoolean(o);
        }
        return this;
    }

    @Override
    public Packer write(final Byte o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByte(o);
        }
        return this;
    }

    @Override
    public Packer write(final Short o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeShort(o);
        }
        return this;
    }

    @Override
    public Packer write(final Character o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeChar(o);
        }
        return this;
    }

    @Override
    public Packer write(final Integer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeInt(o);
        }
        return this;
    }

    @Override
    public Packer write(final Long o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeLong(o);
        }
        return this;
    }

    @Override
    public Packer write(final BigInteger o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o);
        }
        return this;
    }

    @Override
    public Packer write(final BigDecimal o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o.unscaledValue());
            writeInt(o.scale());
        }
        return this;
    }

    @Override
    public Packer write(final Float o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeFloat(o);
        }
        return this;
    }

    @Override
    public Packer write(final Double o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeDouble(o);
        }
        return this;
    }

    @Override
    public Packer write(final byte[] o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o);
        }
        return this;
    }

    @Override
    public Packer write(final byte[] o, final int off, final int len)
            throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o, off, len);
        }
        return this;
    }

    @Override
    public Packer write(final ByteBuffer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteBuffer(o);
        }
        return this;
    }

    @Override
    public Packer write(final String o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeString(o);
        }
        return this;
    }

    @Override
    public Packer writeArrayEnd() throws IOException {
        writeArrayEnd(true);
        return this;
    }

    @Override
    public Packer writeMapEnd() throws IOException {
        writeMapEnd(true);
        return this;
    }

    @Override
    public Packer write(final boolean[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final boolean a : target) {
            writeBoolean(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final short[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final short a : target) {
            writeShort(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final char[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final char a : target) {
            writeChar(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final int[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final int a : target) {
            writeInt(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final long[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final long a : target) {
            writeLong(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final float[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final float a : target) {
            writeFloat(a);
        }
        writeArrayEnd();
        return this;
    }

    @Override
    public Packer write(final double[] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final double a : target) {
            writeDouble(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(boolean[][])
     */

    @Override
    public Packer write(final boolean[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final boolean[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(byte[][])
     */

    @Override
    public Packer write(final byte[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final byte[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(short[][])
     */

    @Override
    public Packer write(final short[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final short[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(char[][])
     */

    @Override
    public Packer write(final char[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final char[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(int[][])
     */

    @Override
    public Packer write(final int[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final int[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(long[][])
     */

    @Override
    public Packer write(final long[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final long[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(float[][])
     */

    @Override
    public Packer write(final float[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final float[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(double[][])
     */

    @Override
    public Packer write(final double[][] target) throws IOException {
        if (target == null) {
            writeNil();
            return this;
        }
        writeArrayBegin(target.length);
        for (final double[] a : target) {
            write(a);
        }
        writeArrayEnd();
        return this;
    }

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    @Override
    public Packer writeIndex(final int index) throws IOException {
        writeInt(index - INDEX_OFFSET);
        return this;
    }

    @Override
    public void close() throws IOException {
    }

    abstract protected void writeBoolean(final boolean v) throws IOException;

    abstract protected void writeByte(final byte v) throws IOException;

    abstract protected void writeShort(final short v) throws IOException;

    abstract protected void writeChar(final char v) throws IOException;

    abstract protected void writeInt(final int v) throws IOException;

    abstract protected void writeLong(final long v) throws IOException;

    abstract protected void writeBigInteger(final BigInteger v)
            throws IOException;

    abstract protected void writeFloat(final float v) throws IOException;

    abstract protected void writeDouble(final double v) throws IOException;

    protected void writeByteArray(final byte[] b) throws IOException {
        writeByteArray(b, 0, b.length);
    }

    abstract protected void writeByteArray(final byte[] b, final int off,
            final int len) throws IOException;

    abstract protected void writeByteBuffer(final ByteBuffer bb)
            throws IOException;

    abstract protected void writeString(final String s) throws IOException;
}
