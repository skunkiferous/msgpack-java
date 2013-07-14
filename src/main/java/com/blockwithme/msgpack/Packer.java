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
import java.io.DataOutput;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Standard serializer in MessagePack for Java. It allows users to serialize
 * objects like <tt>String</tt>, <tt>byte[]</tt>, primitive types and so on.
 * Things <tt>List</tt>, <tt>Map</tt> ... must be built on top of it.
 *
 * It also implements DataOutput, for easier migration of code, but it is
 * unlikely that code using DataOutput can be ported without modifications.
 */
public interface Packer extends Closeable, Flushable, DataOutput {
    /** Writes a boolean. */
    @Override
    void writeBoolean(final boolean o) throws IOException;

    /** Writes a byte. */
    void writeByte(final byte o) throws IOException;

    /** Writes a short. */
    void writeShort(final short o) throws IOException;

    /** Writes a char. */
    void writeChar(final char o) throws IOException;

    /** Writes a int. */
    @Override
    void writeInt(final int o) throws IOException;

    /** Writes a long. */
    @Override
    void writeLong(final long o) throws IOException;

    /** Writes a float. */
    @Override
    void writeFloat(final float o) throws IOException;

    /** Writes a double. */
    @Override
    void writeDouble(final double o) throws IOException;

    /** Writes a boolean array. */
    void writeBooleanArray(final boolean[] o) throws IOException;

    /** Writes a byte array. */
    @Override
    void write(final byte[] o) throws IOException;

    /** Writes a short array. */
    void writeShortArray(final short[] o) throws IOException;

    /** Writes a char array. */
    void writeCharArray(final char[] o) throws IOException;

    /** Writes a int array. */
    void writeIntArray(final int[] o) throws IOException;

    /** Writes a long array. */
    void writeLongArray(final long[] o) throws IOException;

    /** Writes a float array. */
    void writeFloatArray(final float[] o) throws IOException;

    /** Writes a double array. */
    void writeDoubleArray(final double[] o) throws IOException;

    /** Writes a Boolean. */
    void writeBoolean(final Boolean o) throws IOException;

    /** Writes a Byte. */
    void writeByte(final Byte o) throws IOException;

    /** Writes a Short. */
    void writeShort(final Short o) throws IOException;

    /** Writes a Character. */
    void writeCharacter(final Character o) throws IOException;

    /** Writes a Integer. */
    void writeInteger(final Integer o) throws IOException;

    /** Writes a Long. */
    void writeLong(final Long o) throws IOException;

    /** Writes a Float. */
    void writeFloat(final Float o) throws IOException;

    /** Writes a Double. */
    void writeDouble(final Double o) throws IOException;

    /** Writes a BigInteger. */
    void writeBigInteger(final BigInteger o) throws IOException;

    /** Writes a BigDecimal. */
    void writeBigDecimal(final BigDecimal o) throws IOException;

    /** Writes a String. */
    @Override
    void writeUTF(final String o) throws IOException;

    /** Writes a Date out. */
    void writeDate(final Date o) throws IOException;

    /** Writes a ByteBuffer. */
    void writeByteBuffer(final ByteBuffer o) throws IOException;

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    void writeIndex(final int index) throws IOException;

    /** Writes a slice of a byte[]. */
    @Override
    void write(final byte[] o, final int off, final int len) throws IOException;

    /** Writes nil/null. */
    void writeNil() throws IOException;

    /** Writes an array begin. */
    void writeArrayBegin(final int size) throws IOException;

    /** Writes an array end. */
    void writeArrayEnd(final boolean check) throws IOException;

    /** Writes an array end. */
    void writeArrayEnd() throws IOException;

    /** Writes a map begin. */
    void writeMapBegin(final int size) throws IOException;

    /** Writes a map end. */
    void writeMapEnd(final boolean check) throws IOException;

    /** Writes a map end. */
    void writeMapEnd() throws IOException;

    /** Writes an raw begin. */
    void writeRawBegin(final int size) throws IOException;

    /** Writes an raw end. */
    void writeRawEnd() throws IOException;

    /** Writes a byte array *content*. */
    void writePartial(final byte[] o) throws IOException;

    /** Writes a slice of a byte[] *content*. */
    void writePartial(final byte[] o, final int off, final int len)
            throws IOException;

    /** Writes a ByteBuffer *content*. */
    void writePartial(final ByteBuffer o) throws IOException;

    /** Writes a byte. */
    @Override
    @Deprecated
    void writeByte(final int o) throws IOException;

    /** Writes a *byte*. */
    @Override
    @Deprecated
    void write(final int o) throws IOException;

    /** Writes a short. */
    @Override
    @Deprecated
    void writeShort(final int o) throws IOException;

    /** Writes a char. */
    @Override
    @Deprecated
    void writeChar(final int o) throws IOException;

    /** Just delegates to writeUTF(String) */
    @Override
    @Deprecated
    void writeBytes(final String s) throws IOException;

    /** Just delegates to writeUTF(String) */
    @Override
    @Deprecated
    void writeChars(final String s) throws IOException;
}
