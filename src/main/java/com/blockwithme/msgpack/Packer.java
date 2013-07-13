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
 */
public interface Packer extends Closeable, Flushable {
    /** Writes a boolean. */
    Packer write(final boolean o) throws IOException;

    /** Writes a byte. */
    Packer write(final byte o) throws IOException;

    /** Writes a short. */
    Packer write(final short o) throws IOException;

    /** Writes a char. */
    Packer write(final char o) throws IOException;

    /** Writes a int. */
    Packer write(final int o) throws IOException;

    /** Writes a long. */
    Packer write(final long o) throws IOException;

    /** Writes a float. */
    Packer write(final float o) throws IOException;

    /** Writes a double. */
    Packer write(final double o) throws IOException;

    /** Writes a boolean array. */
    Packer write(final boolean[] o) throws IOException;

    /** Writes a byte array. */
    Packer write(final byte[] o) throws IOException;

    /** Writes a short array. */
    Packer write(final short[] o) throws IOException;

    /** Writes a char array. */
    Packer write(final char[] o) throws IOException;

    /** Writes a int array. */
    Packer write(final int[] o) throws IOException;

    /** Writes a long array. */
    Packer write(final long[] o) throws IOException;

    /** Writes a float array. */
    Packer write(final float[] o) throws IOException;

    /** Writes a double array. */
    Packer write(final double[] o) throws IOException;

    /** Writes a Boolean. */
    Packer write(final Boolean o) throws IOException;

    /** Writes a Byte. */
    Packer write(final Byte o) throws IOException;

    /** Writes a Short. */
    Packer write(final Short o) throws IOException;

    /** Writes a Character. */
    Packer write(final Character o) throws IOException;

    /** Writes a Integer. */
    Packer write(final Integer o) throws IOException;

    /** Writes a Long. */
    Packer write(final Long o) throws IOException;

    /** Writes a Float. */
    Packer write(final Float o) throws IOException;

    /** Writes a Double. */
    Packer write(final Double o) throws IOException;

    /** Writes a BigInteger. */
    Packer write(final BigInteger o) throws IOException;

    /** Writes a BigDecimal. */
    Packer write(final BigDecimal o) throws IOException;

    /** Writes a String. */
    Packer write(final String o) throws IOException;

    /** Writes a Date out. */
    Packer write(final Date o) throws IOException;

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    Packer writeIndex(final int index) throws IOException;

    /** Writes a slice of a byte[]. */
    Packer write(final byte[] o, final int off, final int len)
            throws IOException;

    /** Writes a ByteBuffer. */
    Packer write(final ByteBuffer o) throws IOException;

    /** Writes nil/null. */
    Packer writeNil() throws IOException;

    /** Writes an array begin. */
    Packer writeArrayBegin(final int size) throws IOException;

    /** Writes an array end. */
    Packer writeArrayEnd(final boolean check) throws IOException;

    /** Writes an array end. */
    Packer writeArrayEnd() throws IOException;

    /** Writes a map begin. */
    Packer writeMapBegin(final int size) throws IOException;

    /** Writes a map end. */
    Packer writeMapEnd(final boolean check) throws IOException;

    /** Writes a map end. */
    Packer writeMapEnd() throws IOException;

    /** Writes an raw begin. */
    Packer writeRawBegin(final int size) throws IOException;

    /** Writes an raw end. */
    Packer writeRawEnd() throws IOException;

    /** Writes a byte array *content*. */
    Packer writePartial(final byte[] o) throws IOException;

    /** Writes a slice of a byte[] *content*. */
    Packer writePartial(final byte[] o, final int off, final int len)
            throws IOException;

    /** Writes a ByteBuffer *content*. */
    Packer writePartial(final ByteBuffer o) throws IOException;
}
