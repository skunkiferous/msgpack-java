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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Standard deserializer.
 */
public interface Unpacker extends Closeable {

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

    /** Reads a String. */
    String readString() throws IOException;

    /** Reads a BigInteger. */
    BigInteger readBigInteger() throws IOException;

    /** Reads a BigDecimal. */
    BigDecimal readBigDecimal() throws IOException;

    /** Reads a Date. */
    Date readDate() throws IOException;

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
}
