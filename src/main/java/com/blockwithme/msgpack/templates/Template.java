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
package com.blockwithme.msgpack.templates;

import java.io.IOException;

/**
 * Object template, for anything beyond primitive types.
 *
 * When writing an object, we first write some integer ID that tells us what it
 * is. This can be either 0, for null, or a type ID for a new object, or an
 * object ID for a previously serialized object. Since we assume new objects
 * are most frequent, we optimize for the type IDs. And since the positive
 * values are better optimized then the negative ones in message pack, we use
 * positive values for type IDs, and negative values for object IDs. The object
 * IDs are only ever written to the stream, when an object is reused, which we
 * assume to be rarer then new objects.
 */
public interface Template<T> {
    /** Returns the template ID. Will be greater then 0. */
    int getID();

    /** Returns the type that is supported. */
    Class<?> getType();

    /** Returns true, if the template would support reading/writing objects of this type. */
    boolean accept(final Object o);

    /** Returns true if the type is final, or a primitive array. */
    boolean isFinalOrPrimitiveArray();

    /**
     * All objects must be stored in "containers" (list or map). What is the
     * container that should be used for this type? List is the default.
     */
    boolean isListType();

    /** Should we "remember" objects of this type using equality (true), or identity (false)? */
    boolean isMergeable();

    /**
     * Returns a non-negative value, if this type has a "fixed size"
     *
     * @see getSpaceRequired()
     */
    int getFixedSize();

    /**
     * Writes a value. The value can be null. Value type must be checked.
     *
     * First creates the preferred container type (list or map), then write
     * the type ID, then the data, then closes the container.
     *
     * One position must be used for the type ID.
     */
    void writeNonArrayObject(final PackerContext context, final T v)
            throws IOException;

    /** Writes an 1D array of T */
    void write1DArray(final PackerContext context, final T[] v,
            final boolean canContainNullValue) throws IOException;

    /** Writes an 2D array of T */
    void write2DArray(final PackerContext context, final T[][] v,
            final boolean canContainNullValue) throws IOException;

    /** Writes an 3D array of T */
    void write3DArray(final PackerContext context, final T[][][] v,
            final boolean canContainNullValue) throws IOException;

    /**
     * Returns the number of values to write for this (non-null) value.
     * If the preferred container type is a map, this number must be even, so
     * that it can be divided by 2 to give the required map size.
     */
    int getSpaceRequired(final PackerContext context, final T v);

    /**
     * Writes a value's data. The value cannot be null. Value type need not be
     * checked.
     *
     * The number of values written must match "size", the return value of
     * getSpaceRequired(context, v).
     */
    void writeData(final PackerContext context, final int size, final T v)
            throws IOException;

    /**
     * Creating and returning an empty instance before reading enable cycles in
     * the object graph. This method must not return null.
     */
    Object preCreateArray(final int arrayDepth, final int size);

    /**
     * Creating and returning an empty instance before reading enable cycles in
     * the object graph. If possible, it is strongly encouraged to implement it.
     */
    T preCreate(final int size);

    /**
     * Reads a value. The value cannot be null, and must be of the right type,
     * because the type ID was read already, pointing to this template.
     *
     * Note that if isListType() is false, the value written with
     * writeMapHeaderValue() (usually nil), must be read, before the actual map
     * data is read.
     */
    T readData(final UnpackerContext context, final T preCreated, final int size)
            throws IOException;

    /** Reads an 1D array of T */
    void read1DArray(final UnpackerContext context, final T[] preCreated,
            final int size) throws IOException;

    /** Reads an 2D array of T */
    void read2DArray(final UnpackerContext context, final T[][] preCreated,
            final int size) throws IOException;

    /** Reads an 3D array of T */
    void read3DArray(final UnpackerContext context, final T[][][] preCreated,
            final int size) throws IOException;
}
