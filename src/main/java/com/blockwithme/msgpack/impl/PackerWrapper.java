/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.msgpack.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;

import com.blockwithme.msgpack.Packer;

/**
 * Wrapper for Packer
 *
 * @author monster
 */
public class PackerWrapper implements Packer {

    /** The real Packer. */
    protected final Packer packer;

    /**
     *
     */
    public PackerWrapper(final Packer packer) {
        this.packer = Objects.requireNonNull(packer);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#packer()
     */
    public Packer packer() {
        return packer;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(boolean)
     */

    @Override
    public Packer write(final boolean o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(byte)
     */

    @Override
    public Packer write(final byte o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(short)
     */

    @Override
    public Packer write(final short o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(char)
     */

    @Override
    public Packer write(final char o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(int)
     */

    @Override
    public Packer write(final int o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(long)
     */

    @Override
    public Packer write(final long o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(float)
     */

    @Override
    public Packer write(final float o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(double)
     */

    @Override
    public Packer write(final double o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Boolean)
     */

    @Override
    public Packer write(final Boolean o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Byte)
     */

    @Override
    public Packer write(final Byte o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Short)
     */

    @Override
    public Packer write(final Short o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Character)
     */

    @Override
    public Packer write(final Character o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Integer)
     */

    @Override
    public Packer write(final Integer o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Long)
     */

    @Override
    public Packer write(final Long o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Float)
     */

    @Override
    public Packer write(final Float o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.Double)
     */

    @Override
    public Packer write(final Double o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.math.BigInteger)
     */

    @Override
    public Packer write(final BigInteger o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.math.BigDecimal)
     */

    @Override
    public Packer write(final BigDecimal o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(boolean[])
     */

    @Override
    public Packer write(final boolean[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(byte[])
     */

    @Override
    public Packer write(final byte[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(short[])
     */

    @Override
    public Packer write(final short[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(char[])
     */

    @Override
    public Packer write(final char[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(int[])
     */

    @Override
    public Packer write(final int[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(long[])
     */

    @Override
    public Packer write(final long[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(float[])
     */

    @Override
    public Packer write(final float[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(double[])
     */

    @Override
    public Packer write(final double[] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(boolean[][])
     */

    @Override
    public Packer write(final boolean[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(byte[][])
     */

    @Override
    public Packer write(final byte[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(short[][])
     */

    @Override
    public Packer write(final short[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(char[][])
     */

    @Override
    public Packer write(final char[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(int[][])
     */

    @Override
    public Packer write(final int[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(long[][])
     */

    @Override
    public Packer write(final long[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(float[][])
     */

    @Override
    public Packer write(final float[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(double[][])
     */

    @Override
    public Packer write(final double[][] o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.lang.String)
     */

    @Override
    public Packer write(final String o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(java.nio.ByteBuffer)
     */

    @Override
    public Packer write(final ByteBuffer o) throws IOException {
        if (doWrite(o)) {
            packer.write(o);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#write(byte[], int, int)
     */

    @Override
    public Packer write(final byte[] o, final int off, final int len)
            throws IOException {
        packer.write(o, off, len);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeNil()
     */

    @Override
    public Packer writeNil() throws IOException {
        packer.writeNil();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeArrayBegin(int)
     */

    @Override
    public Packer writeArrayBegin(final int size) throws IOException {
        packer.writeArrayBegin(size);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeArrayEnd(boolean)
     */

    @Override
    public Packer writeArrayEnd(final boolean check) throws IOException {
        packer.writeArrayEnd(check);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeArrayEnd()
     */

    @Override
    public Packer writeArrayEnd() throws IOException {
        packer.writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeMapBegin(int)
     */

    @Override
    public Packer writeMapBegin(final int size) throws IOException {
        packer.writeMapBegin(size);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeMapEnd(boolean)
     */

    @Override
    public Packer writeMapEnd(final boolean check) throws IOException {
        packer.writeMapEnd(check);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Packer#writeMapEnd()
     */

    @Override
    public Packer writeMapEnd() throws IOException {
        packer.writeMapEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        packer.close();
    }

    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
        packer.flush();
    }

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    @Override
    public Packer writeIndex(final int index) throws IOException {
        packer.writeIndex(index);
        return this;
    }

    /**
     * Returns false, if the object has been "replaced", and does not need to be written.
     * Called for every Object, except primitive wrappers.
     * @throws IOException
     */
    protected boolean doWrite(final Object o) throws IOException {
        return false;
    }
}
