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

    /**
     * Writes a value. The value cannot be null. The Template must check the
     * type matching, and write first the type ID.
     */
    void checkAndWrite(final PackerContext context, final T v)
            throws IOException;

    /**
     * Writes a value. The value cannot be null and will be of the right type.
     * Writes first the type ID.
     */
    void write(final PackerContext context, final T v) throws IOException;

    /**
     * Writes a value. The value cannot be null and will be of the right type.
     * No ID is written.
     */
    void writeNoID(final PackerContext context, final T v) throws IOException;

    /**
     * Reads a value. The value cannot be null, and must be of the right type,
     * because the type ID was read already, pointing to this template.
     */
    T read(final UnpackerContext context) throws IOException;
}
