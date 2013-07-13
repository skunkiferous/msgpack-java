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

import com.blockwithme.msgpack.Unpacker;

/**
 * The Abstract Unpacker reuses the code form the original Java implementation
 * of the Abstract Unpacker.
 *
 * It is limited to the core protocol implementation, without support for Java
 * objects.
 *
 * @author monster
 */
public abstract class AbstractUnpacker implements Unpacker {
    protected int rawSizeLimit = 134217728;

    protected int arraySizeLimit = 4194304;

    protected int mapSizeLimit = 2097152;

    /** Reads a Date. */
    @Override
    public Date readDate() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        return new Date(readLong());
    }

    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        return ByteBuffer.wrap(readByteArray());
    }

    @Override
    public void readArrayEnd() throws IOException {
        readArrayEnd(false);
    }

    @Override
    public void readMapEnd() throws IOException {
        readMapEnd(false);
    }

    @Override
    public void setRawSizeLimit(final int size) {
        if (size < 32) {
            rawSizeLimit = 32;
        } else {
            rawSizeLimit = size;
        }
    }

    @Override
    public void setArraySizeLimit(final int size) {
        if (size < 16) {
            arraySizeLimit = 16;
        } else {
            arraySizeLimit = size;
        }
    }

    @Override
    public void setMapSizeLimit(final int size) {
        if (size < 16) {
            mapSizeLimit = 16;
        } else {
            mapSizeLimit = size;
        }
    }

    @Override
    public boolean[] readBooleanArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final boolean[] result = new boolean[n];
        for (int i = 0; i < n; i++) {
            result[i] = readBoolean();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public short[] readShortArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final short[] result = new short[n];
        for (int i = 0; i < n; i++) {
            result[i] = readShort();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public char[] readCharArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final char[] result = new char[n];
        for (int i = 0; i < n; i++) {
            result[i] = readChar();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public int[] readIntArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = readInt();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public long[] readLongArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final long[] result = new long[n];
        for (int i = 0; i < n; i++) {
            result[i] = readLong();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public float[] readFloatArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = readFloat();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public double[] readDoubleArray() throws IOException {
        if (trySkipNil()) {
            return null;
        }
        final int n = readArrayBegin();
        final double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = readDouble();
        }
        readArrayEnd();
        return result;
    }

    @Override
    public BigDecimal readBigDecimal() throws IOException {
        final BigInteger unscaledVal = readBigInteger(false);
        if (unscaledVal == null) {
            return null;
        }
        final int scale = readInt();
        return new BigDecimal(unscaledVal, scale);
    }

    /** Reads an index written with Packer.writeIndex(int). */
    @Override
    public int readIndex() throws IOException {
        return readInt() + AbstractPacker.INDEX_OFFSET;
    }

    protected abstract BigInteger readBigInteger(final boolean countAaValue)
            throws IOException;

    protected abstract boolean tryReadNil() throws IOException;
}
