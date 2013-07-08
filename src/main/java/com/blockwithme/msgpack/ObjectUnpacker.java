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
package com.blockwithme.msgpack;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import com.blockwithme.msgpack.templates.Template;

/**
 * ObjectUnpacker can unpack any object with a registered template.
 * It does NOT extends Unpacker by design. It has the same interface,
 * but by not extending Unpacker, we can eliminate the need for a virtual
 * call.
 *
 * @author monster
 */
public interface ObjectUnpacker extends Closeable {
    // First, the Unpacker methods ...

    /** Reads a boolean. */
    boolean readBoolean() throws IOException;

    /** Reads a byte. */
    byte readByte() throws IOException;

    /** Reads a short. */
    short readShort() throws IOException;

    /** Reads a char. */
    char readChar() throws IOException;

    /** Reads an int. */
    int readInt() throws IOException;

    /** Reads a long. */
    long readLong() throws IOException;

    /** Reads a float. */
    float readFloat() throws IOException;

    /** Reads a double. */
    double readDouble() throws IOException;

    /** Reads a boolean array. */
    boolean[] readBooleanArray() throws IOException;

    /** Reads a byte array. */
    byte[] readByteArray() throws IOException;

    /** Reads a short array. */
    short[] readShortArray() throws IOException;

    /** Reads a char array. */
    char[] readCharArray() throws IOException;

    /** Reads a int array. */
    int[] readIntArray() throws IOException;

    /** Reads a long array. */
    long[] readLongArray() throws IOException;

    /** Reads a float array. */
    float[] readFloatArray() throws IOException;

    /** Reads a double array. */
    double[] readDoubleArray() throws IOException;

    /** Reads a boolean array array. */
    boolean[][] readBooleanArrayArray() throws IOException;

    /** Reads a byte array array. */
    byte[][] readByteArrayArray() throws IOException;

    /** Reads a short array array. */
    short[][] readShortArrayArray() throws IOException;

    /** Reads a char array array. */
    char[][] readCharArrayArray() throws IOException;

    /** Reads a int array array. */
    int[][] readIntArrayArray() throws IOException;

    /** Reads a long array array. */
    long[][] readLongArrayArray() throws IOException;

    /** Reads a float array array. */
    float[][] readFloatArrayArray() throws IOException;

    /** Reads a double array array. */
    double[][] readDoubleArrayArray() throws IOException;

    /** Reads a String. */
    String readString() throws IOException;

    /** Reads a BigInteger. */
    BigInteger readBigInteger() throws IOException;

    /** Reads a BigDecimal. */
    BigDecimal readBigDecimal() throws IOException;

    /** Reads a ByteBuffer. */
    ByteBuffer readByteBuffer() throws IOException;

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

    /** Reads an index written with Packer.writeIndex(int). */
    int readIndex() throws IOException;

    /** Reads a nil/null. */
    void readNil() throws IOException;

    /** Returns true, if a nil/null could be read. */
    boolean trySkipNil() throws IOException;

    // Now our own methods ...

    /** Returns the underlying unpacker. */
    Unpacker unpacker();

    /** Reads any Object. */
    Object readObject() throws IOException;

    /** Reads any Object. Fails if Object type does not match template type. */
    <T> T readObject(final Template<T> template) throws IOException;

    /**
     * Reads any Object.
     *
     * Object must be compatible with non-null template, but it is not checked.
     * Object must not be null, but it is not checked.
     * Object must not be reused anywhere else, but it is not checked.
     * No ID is read.
     *
     * Fastest, but least safe.
     */
    <T> T readUnsharedUncheckedNonNullNoID(final Template<T> template)
            throws IOException;

    /** Reads a Class. */
    Class<?> readClass() throws IOException;

    /** Reads a Date. */
    Date readDate() throws IOException;

    /** Reads an Enum. */
    Enum<?> readEnum() throws IOException;
}
