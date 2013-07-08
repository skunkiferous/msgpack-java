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
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import com.blockwithme.msgpack.templates.Template;

/**
 * ObjectPacker can pack any object with a registered template.
 *
 * @author monster
 */
public interface ObjectPacker extends Closeable, Flushable {
    // First, the Packer methods ...

    /** Writes a boolean. */
    ObjectPacker write(final boolean o) throws IOException;

    /** Writes a byte. */
    ObjectPacker write(final byte o) throws IOException;

    /** Writes a short. */
    ObjectPacker write(final short o) throws IOException;

    /** Writes a char. */
    ObjectPacker write(final char o) throws IOException;

    /** Writes a int. */
    ObjectPacker write(final int o) throws IOException;

    /** Writes a long. */
    ObjectPacker write(final long o) throws IOException;

    /** Writes a float. */
    ObjectPacker write(final float o) throws IOException;

    /** Writes a double. */
    ObjectPacker write(final double o) throws IOException;

    /** Writes a boolean array. */
    ObjectPacker write(final boolean[] o) throws IOException;

    /** Writes a byte array. */
    ObjectPacker write(final byte[] o) throws IOException;

    /** Writes a short array. */
    ObjectPacker write(final short[] o) throws IOException;

    /** Writes a char array. */
    ObjectPacker write(final char[] o) throws IOException;

    /** Writes a int array. */
    ObjectPacker write(final int[] o) throws IOException;

    /** Writes a long array. */
    ObjectPacker write(final long[] o) throws IOException;

    /** Writes a float array. */
    ObjectPacker write(final float[] o) throws IOException;

    /** Writes a double array. */
    ObjectPacker write(final double[] o) throws IOException;

    /** Writes a boolean array array. */
    ObjectPacker write(final boolean[][] o) throws IOException;

    /** Writes a byte array array. */
    ObjectPacker write(final byte[][] o) throws IOException;

    /** Writes a short array array. */
    ObjectPacker write(final short[][] o) throws IOException;

    /** Writes a char array array. */
    ObjectPacker write(final char[][] o) throws IOException;

    /** Writes a int array array. */
    ObjectPacker write(final int[][] o) throws IOException;

    /** Writes a long array array. */
    ObjectPacker write(final long[][] o) throws IOException;

    /** Writes a float array array. */
    ObjectPacker write(final float[][] o) throws IOException;

    /** Writes a double array array. */
    ObjectPacker write(final double[][] o) throws IOException;

    /** Writes a Boolean. */
    ObjectPacker write(final Boolean o) throws IOException;

    /** Writes a Byte. */
    ObjectPacker write(final Byte o) throws IOException;

    /** Writes a Short. */
    ObjectPacker write(final Short o) throws IOException;

    /** Writes a Character. */
    ObjectPacker write(final Character o) throws IOException;

    /** Writes a Integer. */
    ObjectPacker write(final Integer o) throws IOException;

    /** Writes a Long. */
    ObjectPacker write(final Long o) throws IOException;

    /** Writes a Float. */
    ObjectPacker write(final Float o) throws IOException;

    /** Writes a Double. */
    ObjectPacker write(final Double o) throws IOException;

    /** Writes a BigInteger. */
    ObjectPacker write(final BigInteger o) throws IOException;

    /** Writes a BigDecimal. */
    ObjectPacker write(final BigDecimal o) throws IOException;

    /** Writes a String. */
    ObjectPacker write(final String o) throws IOException;

    /** Writes a byte[]. */
    ObjectPacker write(final byte[] o, final int off, final int len)
            throws IOException;

    /** Writes a ByteBuffer. */
    ObjectPacker write(final ByteBuffer o) throws IOException;

    /** Writes nil/null. */
    ObjectPacker writeNil() throws IOException;

    /** Writes an array begin. */
    ObjectPacker writeArrayBegin(final int size) throws IOException;

    /** Writes an array end. */
    ObjectPacker writeArrayEnd(final boolean check) throws IOException;

    /** Writes an array end. */
    ObjectPacker writeArrayEnd() throws IOException;

    /** Writes a map begin. */
    ObjectPacker writeMapBegin(final int size) throws IOException;

    /** Writes a map end. */
    ObjectPacker writeMapEnd(final boolean check) throws IOException;

    /** Writes a map end. */
    ObjectPacker writeMapEnd() throws IOException;

    /**
     * Deduce 31 from the index, so it is more likely to be saved as one byte.
     * This methods should be used for normally non-negative integers.
     * -1 also stores as one byte.
     */
    ObjectPacker writeIndex(final int index) throws IOException;

    // The our own methods ...

    /** Returns the underlying packer, if any, otherwise self. */
    Packer packer();

    /**
     * Writes an Object out. Template is determined based on object type.
     * Pass along a template for faster results.
     */
    <E> ObjectPacker write(final E o) throws IOException;

    /** Writes an Object out. Object must be compatible with non-null template. */
    <E> ObjectPacker write(final E o, final Template<E> template)
            throws IOException;

    /**
     * Writes an Object out.
     *
     * Object must be compatible with non-null template, but it is not checked.
     * Object must not be null, but it is not checked.
     * Object must not be reused anywhere else, but it is not checked.
     * No type ID is written.
     *
     * Fastest, but least safe.
     */
    <E> ObjectPacker writeUnsharedUncheckedNonNullNoID(final E o,
            final Template<E> template) throws IOException;

    /**
     * Writes an Object array out.
     * Note that this method can write *any* Object[], of any type, even String[][]
     */
    ObjectPacker write(final Object[] o) throws IOException;

    /** Writes a Class out. */
    ObjectPacker write(final Class<?> o) throws IOException;

    /** Writes a Date out. */
    ObjectPacker write(final Date o) throws IOException;

    /** Writes a Enum out. */
    ObjectPacker write(final Enum<?> o) throws IOException;
}
