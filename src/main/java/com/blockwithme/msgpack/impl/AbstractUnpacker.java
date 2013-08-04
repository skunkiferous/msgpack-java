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
    public BigDecimal readBigDecimal() throws IOException {
        final BigInteger unscaledVal = readBigInteger(false);
        if (unscaledVal == null) {
            return null;
        }
        final int scale = readInt();
        return new BigDecimal(unscaledVal, scale);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readUnsignedByte()
     */
    @Override
    @Deprecated
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xFF;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readUnsignedShort()
     */
    @Override
    @Deprecated
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xFFFF;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLine()
     */
    @Override
    @Deprecated
    public String readLine() throws IOException {
        return readUTF();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#skipBytes(int)
     */
    @Override
    @Deprecated
    public int skipBytes(final int n) throws IOException {
        final byte[] raw = readByteArray();
        return (raw == null) ? -1 : raw.length;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFully(byte[])
     */
    @Override
    @Deprecated
    public void readFully(final byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFully(byte[], int, int)
     */
    @Override
    @Deprecated
    public void readFully(final byte[] b, final int off, final int length)
            throws IOException {
        final byte[] raw = readByteArray();
        if ((raw == null) || (raw.length != length)) {
            throw new IOException("Could not read " + length + " bytes raw");
        }
        System.arraycopy(raw, 0, b, off, length);
    }

    /** Reads an index written with Packer.writeIndex(int). */
    @Override
    public int readIndex() throws IOException {
        return readInt() + AbstractPacker.INDEX_OFFSET;
    }

    /** Checks if we are within a raw read, and fails if not. */
    protected abstract void checkInRawRead() throws IOException;

    protected abstract BigInteger readBigInteger(final boolean countAaValue)
            throws IOException;

    protected abstract boolean tryReadNil() throws IOException;
}
