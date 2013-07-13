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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import com.blockwithme.msgpack.templates.Template;

/**
 * ObjectPacker can pack any object with a registered template.
 *
 * It does NOT extends Packer by design. To serialize primitive value, go
 * directly to the underlying Packer, with the packer() method.
 *
 * Please realize the difference between the write(int[]) of Packer and our
 * writeObject(int[]); ours stores additional information to allow for generic object
 * serialization (the type), therefore the two are not compatible. Also, all
 * the methods here take into consideration that an object might be serialized
 * multiple times, and so writing the same int[] with an ObjectPacker will
 * result in a single copy on the stream, while it would result in multiple
 * copies if done using Unpacker.
 *
 * @author monster
 */
public interface ObjectPacker {

    /** Returns the underlying packer. */
    Packer packer();

    /** Writes a Boolean. */
    ObjectPacker writeObject(final Boolean o) throws IOException;

    /** Writes a Byte. */
    ObjectPacker writeObject(final Byte o) throws IOException;

    /** Writes a Short. */
    ObjectPacker writeObject(final Short o) throws IOException;

    /** Writes a Character. */
    ObjectPacker writeObject(final Character o) throws IOException;

    /** Writes a Integer. */
    ObjectPacker writeObject(final Integer o) throws IOException;

    /** Writes a Long. */
    ObjectPacker writeObject(final Long o) throws IOException;

    /** Writes a Float. */
    ObjectPacker writeObject(final Float o) throws IOException;

    /** Writes a Double. */
    ObjectPacker writeObject(final Double o) throws IOException;

    /** Writes a BigInteger. */
    ObjectPacker writeObject(final BigInteger o) throws IOException;

    /** Writes a BigDecimal. */
    ObjectPacker writeObject(final BigDecimal o) throws IOException;

    /** Writes a Date out. */
    ObjectPacker writeObject(final Date o) throws IOException;

    /** Writes a boolean array. */
    ObjectPacker writeObject(final boolean[] o) throws IOException;

    /** Writes a byte array. */
    ObjectPacker writeObject(final byte[] o) throws IOException;

    /** Writes a short array. */
    ObjectPacker writeObject(final short[] o) throws IOException;

    /** Writes a char array. */
    ObjectPacker writeObject(final char[] o) throws IOException;

    /** Writes a int array. */
    ObjectPacker writeObject(final int[] o) throws IOException;

    /** Writes a long array. */
    ObjectPacker writeObject(final long[] o) throws IOException;

    /** Writes a float array. */
    ObjectPacker writeObject(final float[] o) throws IOException;

    /** Writes a double array. */
    ObjectPacker writeObject(final double[] o) throws IOException;

    /** Writes a String. */
    ObjectPacker writeObject(final String o) throws IOException;

    /** Writes a slice of a byte[]. */
    ObjectPacker writeObject(final byte[] o, final int off, final int len)
            throws IOException;

    /** Writes a ByteBuffer. */
    ObjectPacker writeObject(final ByteBuffer o) throws IOException;

    /**
     * Writes an Object out. Template is determined based on object type.
     * Pass along a template for faster results.
     */
    ObjectPacker writeObject(final Object o) throws IOException;

    /**
     * Writes an Object out. Object must be compatible with non-null template.
     * Note that we cannot link the Object type with the template type, since
     * we could use the template to also write an *array* of the object type.
     */
    ObjectPacker writeObject(final Object o, final Template<?> template)
            throws IOException;

    /** Writes a Class out. */
    ObjectPacker writeObject(final Class<?> o) throws IOException;
}
