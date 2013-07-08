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

import com.blockwithme.msgpack.Unpacker;
import com.blockwithme.msgpack.ValueType;

/**
 * Wrapper for Unpacker.
 *
 * @author monster
 */
public class UnpackerWrapper implements Unpacker {

    /** The real Unpacker */
    protected final Unpacker unpacker;

    /**
     * Creates a new UnpackerWrapper.
     */
    public UnpackerWrapper(final Unpacker unpacker) {
        this.unpacker = Objects.requireNonNull(unpacker);
    }

    /** Returns the underlying unpacker, if any, otherwise self. */
    public Unpacker unpacker() {
        return unpacker;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBoolean()
     */
    @Override
    public boolean readBoolean() throws IOException {
        return unpacker.readBoolean();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByte()
     */
    @Override
    public byte readByte() throws IOException {
        return unpacker.readByte();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShort()
     */
    @Override
    public short readShort() throws IOException {
        return unpacker.readShort();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readChar()
     */
    @Override
    public char readChar() throws IOException {
        return unpacker.readChar();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readInt()
     */
    @Override
    public int readInt() throws IOException {
        return unpacker.readInt();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLong()
     */
    @Override
    public long readLong() throws IOException {
        return unpacker.readLong();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloat()
     */
    @Override
    public float readFloat() throws IOException {
        return unpacker.readFloat();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDouble()
     */
    @Override
    public double readDouble() throws IOException {
        return unpacker.readDouble();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigInteger()
     */
    @Override
    public BigInteger readBigInteger() throws IOException {
        final BigInteger obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readBigInteger(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigDecimal()
     */
    @Override
    public BigDecimal readBigDecimal() throws IOException {
        final BigDecimal obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readBigDecimal(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBooleanArray()
     */
    @Override
    public boolean[] readBooleanArray() throws IOException {
        final boolean[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readBooleanArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteArray()
     */
    @Override
    public byte[] readByteArray() throws IOException {
        final byte[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readByteArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShortArray()
     */
    @Override
    public short[] readShortArray() throws IOException {
        final short[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readShortArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readCharArray()
     */
    @Override
    public char[] readCharArray() throws IOException {
        final char[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readCharArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readIntArray()
     */
    @Override
    public int[] readIntArray() throws IOException {
        final int[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readIntArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLongArray()
     */
    @Override
    public long[] readLongArray() throws IOException {
        final long[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readLongArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloatArray()
     */
    @Override
    public float[] readFloatArray() throws IOException {
        final float[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readFloatArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDoubleArray()
     */
    @Override
    public double[] readDoubleArray() throws IOException {
        final double[] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readDoubleArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBooleanArrayArray()
     */
    @Override
    public boolean[][] readBooleanArrayArray() throws IOException {
        final boolean[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readBooleanArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteArrayArray()
     */
    @Override
    public byte[][] readByteArrayArray() throws IOException {
        final byte[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readByteArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShortArrayArray()
     */
    @Override
    public short[][] readShortArrayArray() throws IOException {
        final short[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readShortArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readCharArrayArray()
     */
    @Override
    public char[][] readCharArrayArray() throws IOException {
        final char[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readCharArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readIntArrayArray()
     */
    @Override
    public int[][] readIntArrayArray() throws IOException {
        final int[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readIntArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLongArrayArray()
     */
    @Override
    public long[][] readLongArrayArray() throws IOException {
        final long[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readLongArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloatArrayArray()
     */
    @Override
    public float[][] readFloatArrayArray() throws IOException {
        final float[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readFloatArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDoubleArrayArray()
     */
    @Override
    public double[][] readDoubleArrayArray() throws IOException {
        final double[][] obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readDoubleArrayArray(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readString()
     */
    @Override
    public String readString() throws IOException {
        final String obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readString(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteBuffer()
     */
    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        final ByteBuffer obj = read();
        if (obj != null) {
            return obj;
        }
        final int index = nextIndex();
        return read(unpacker.readByteBuffer(), index);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#getNextType()
     */
    @Override
    public ValueType getNextType() throws IOException {
        return unpacker.getNextType();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setRawSizeLimit(int)
     */
    @Override
    public void setRawSizeLimit(final int size) {
        unpacker.setRawSizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setArraySizeLimit(int)
     */
    @Override
    public void setArraySizeLimit(final int size) {
        unpacker.setArraySizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setMapSizeLimit(int)
     */
    @Override
    public void setMapSizeLimit(final int size) {
        unpacker.setMapSizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#skip()
     */
    @Override
    public void skip() throws IOException {
        unpacker.skip();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayBegin()
     */
    @Override
    public int readArrayBegin() throws IOException {
        return unpacker.readArrayBegin();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayEnd(boolean)
     */
    @Override
    public void readArrayEnd(final boolean check) throws IOException {
        unpacker.readArrayEnd(check);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayEnd()
     */
    @Override
    public void readArrayEnd() throws IOException {
        unpacker.readArrayEnd();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapBegin()
     */
    @Override
    public int readMapBegin() throws IOException {
        return unpacker.readMapBegin();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapEnd(boolean)
     */
    @Override
    public void readMapEnd(final boolean check) throws IOException {
        unpacker.readMapEnd(check);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapEnd()
     */
    @Override
    public void readMapEnd() throws IOException {
        unpacker.readMapEnd();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readNil()
     */
    @Override
    public void readNil() throws IOException {
        unpacker.readNil();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#trySkipNil()
     */
    @Override
    public boolean trySkipNil() throws IOException {
        return unpacker.trySkipNil();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        unpacker.close();
    }

    /** Reads an index written with Packer.writeIndex(int). */
    @Override
    public int readIndex() throws IOException {
        return unpacker.readIndex();
    }

    /** Can return a previously read, and cached, object.
     * @throws IOException */
    protected <E> E read() throws IOException {
        return null;
    }

    /** Returns the index of the next object to be read. */
    protected int nextIndex() {
        return 0;
    }

    /** We have just read this object. */
    protected <E> E read(final E obj, final int index) {
        return obj;
    }
}
