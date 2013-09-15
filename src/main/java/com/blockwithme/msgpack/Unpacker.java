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
package com.blockwithme.msgpack;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Standard deserializer, implementing the core Message-Pack protocol.
 *
 * It also implements DataInput, for easier migration of code, but it is
 * unlikely that code using DataInput can be ported without modifications.
 */
public interface Unpacker extends Closeable, DataInput {

    /** Reads a boolean. */
    @Override
    boolean readBoolean() throws IOException;

    /** Reads a byte. */
    @Override
    byte readByte() throws IOException;

    /** Reads a short. */
    @Override
    short readShort() throws IOException;

    /** Reads a char. */
    @Override
    char readChar() throws IOException;

    /** Reads an int. */
    @Override
    int readInt() throws IOException;

    /** Reads a long. */
    @Override
    long readLong() throws IOException;

    /** Reads a float. */
    @Override
    float readFloat() throws IOException;

    /** Reads a double. */
    @Override
    double readDouble() throws IOException;

    /** Reads a String. */
    @Override
    String readUTF() throws IOException;

    /** Reads a BigInteger. */
    BigInteger readBigInteger() throws IOException;

    /** Reads a BigDecimal. */
    BigDecimal readBigDecimal() throws IOException;

    /** Reads a Date. */
    Date readDate() throws IOException;

    /** Reads a byte array. */
    byte[] readByteArray() throws IOException;

    /** Reads a ByteBuffer. */
    ByteBuffer readByteBuffer() throws IOException;

    /** Reads an index written with Packer.writeIndex(int). */
    int readIndex() throws IOException;

    /** Returns the type of the next value to be read. */
    ValueType getNextType() throws IOException;

    /** Set the raw size limit. */
    void setRawSizeLimit(final int size);

    /** Set the array size limit. */
    void setArraySizeLimit(final int size);

    /** Set the map size limit. */
    void setMapSizeLimit(final int size);

    /** Skip the next value. */
    void skip() throws IOException;

    /** Reads an array begin. */
    int readArrayBegin() throws IOException;

    /** Reads an array end. */
    void readArrayEnd(final boolean check) throws IOException;

    /** Reads an array end. */
    void readArrayEnd() throws IOException;

    /** Reads a map begin. */
    int readMapBegin() throws IOException;

    /** Reads a map end. */
    void readMapEnd(final boolean check) throws IOException;

    /** Reads a map end. */
    void readMapEnd() throws IOException;

    /** Reads a nil/null. */
    void readNil() throws IOException;

    /** Returns true, if a nil/null could be read. */
    boolean trySkipNil() throws IOException;

    /** Reads a raw begin and return the size. */
    int readRawBegin() throws IOException;

    /** Reads a raw end. */
    void readRawEnd() throws IOException;

    /**
     * Returns the underlying DataInput: use with extreme care!
     *
     * Before using the underlying DataInput, you must first call readRawBegin()
     * which will tell you how many bytes you must read. While reading, or after
     * you are done, you must call rawRead(read), so the Unpacker knows how much
     * you read. When you are done, you must call readRawEnd(). If the wrong
     * amount of data was read, according to rawRead(), readRawEnd() will fail.
     * @throws IOException
     */
    DataInput dataInput() throws IOException;

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
    InputStream inputStream() throws IOException;

    /**
     * Reads a ByteBuffer *content*, between readRawBegin() and
     * readRawEnd(). It can be used together with dataInput(). Note that
     * readPartialByteBuffer() calls rawRead() directly, while you need to
     * do this yourself if using dataInput().
     *
     * This method is just an helper, since DataInput does not directly support
     * ByteBuffers.
     */
    ByteBuffer readPartialByteBuffer(final int bytes) throws IOException;

    /**
     * Indicate that 'read' bytes were read to the underlying DataInput.
     * You must call this method, when accessing the underlying DataInput directly.
     * @throws IOException
     */
    void rawRead(final int read) throws IOException;

    // For DataInput compatibility; not recommended for new code.

    /** readByte() & 0xFF */
    @Override
    @Deprecated
    int readUnsignedByte() throws IOException;

    /** readShort() & 0xFFFF */
    @Override
    @Deprecated
    int readUnsignedShort() throws IOException;

    /** Just delegates to readUTF() */
    @Override
    @Deprecated
    String readLine() throws IOException;

    /** Just delegates to readFully(byte[], 0, byte[].length) */
    @Override
    @Deprecated
    void readFully(final byte b[]) throws IOException;

    /** Reads a raw into b. The raw must be exactly of length len. */
    @Override
    @Deprecated
    void readFully(final byte b[], final int off, final int length)
            throws IOException;

    /** Skips a raw. n is ignored. The raw size is returned. */
    @Override
    @Deprecated
    int skipBytes(final int n) throws IOException;

}
