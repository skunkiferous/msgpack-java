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
 * ObjectUnpacker can unpack any object with a registered template.
 * It does NOT extends Unpacker by design. To serialize primitive value, go
 * directly to the underlying Unpacker, with the unpacker() method.
 *
 * Please realize the difference between the int[] readIntArray() of Unpacker
 * and ours; ours stores additional information to allow for generic object
 * serialization (the type), therefore the two are not compatible. Also, all
 * the methods here take into consideration that an object might be serialized
 * multiple times, and so writing the same int[] with an ObjectPacker will
 * result in a single copy on the stream, while it would result in multiple
 * copies if done using Unpacker.
 *
 * @author monster
 */
public interface ObjectUnpacker {

    /** Returns the underlying unpacker. */
    Unpacker unpacker();

    /** Reads any Object. */
    Object readObject() throws IOException;

    /**
     * Reads any Object. Fails if the template is not null, and the Object type
     * does not match template type. Since templates are used to read not only
     * objects, but also array, we cannot assume that for a Template<T>, the
     * return type will be T; it could also be T[] ...
     */
    Object readObject(final Template<?> template) throws IOException;

    /** Reads any Object. */
    Object readObject(final boolean ifObjectArrayCanContainNullValue)
            throws IOException;

    /**
     * Reads any Object. Fails if the template is not null, and the Object type
     * does not match template type. Since templates are used to read not only
     * objects, but also array, we cannot assume that for a Template<T>, the
     * return type will be T; it could also be T[] ...
     */
    Object readObject(final Template<?> template,
            final boolean ifObjectArrayCanContainNullValue) throws IOException;

    /** Reads a boolean. */
    Boolean readBoolean() throws IOException;

    /** Reads a byte. */
    Byte readByte() throws IOException;

    /** Reads a short. */
    Short readShort() throws IOException;

    /** Reads a char. */
    Character readChar() throws IOException;

    /** Reads an int. */
    Integer readInt() throws IOException;

    /** Reads a long. */
    Long readLong() throws IOException;

    /** Reads a float. */
    Float readFloat() throws IOException;

    /** Reads a double. */
    Double readDouble() throws IOException;

    /** Reads a String. */
    String readString() throws IOException;

    /** Reads a BigInteger. */
    BigInteger readBigInteger() throws IOException;

    /** Reads a BigDecimal. */
    BigDecimal readBigDecimal() throws IOException;

    /** Reads a Date. */
    Date readDate() throws IOException;

    /** Reads a Class. */
    Class<?> readClass() throws IOException;

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
}
