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
package com.blockwithme.msgpack.templates;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import com.blockwithme.msgpack.ObjectPacker;
import com.blockwithme.msgpack.Packer;
import com.blockwithme.msgpack.Unpacker;
import com.blockwithme.msgpack.ValueType;

/**
 * Object template, for anything beyond primitive types.
 *
 * @author monster
 */
public abstract class AbstractTemplate<T> implements Template<T> {

    /** The ClassNameConverter */
    private static volatile ClassNameConverter CLASS_NAME_CONVERTER = new DefaultClassNameConverter();

    /** The template ID. */
    protected final int id;

    /** The type that is supported. */
    protected final Class<T> type;

    /** The 1D array type that is supported. */
    protected final Class<T[]> type1D;

    /** The 2D array type that is supported. */
    protected final Class<T[][]> type2D;

    /** Returns true if the type is final, or a primitive array. */
    protected final boolean isFinalOrPrimitiveArray;

    /**
     * All objects must be stored in "containers" (list or map). What is the
     * container that should be used for this type? List is the default.
     */
    protected final boolean isListType;

    /** Should we "remember" objects of this type using equality (true), or identity (false)? */
    protected final boolean isMergeable;

    /** A non-negative value, if this type has a "fixed size" */
    private final int fixedSize;

    /** Sets the ClassNameConverter. */
    public static void setClassNameConverter(final ClassNameConverter cnc) {
        CLASS_NAME_CONVERTER = Objects.requireNonNull(cnc);
    }

    /** Returns the ClassNameConverter. */
    public static ClassNameConverter getClassNameConverter() {
        return CLASS_NAME_CONVERTER;
    }

    /** Reads any Object. We assume the template is valid. */
    private static <T> T readNewNonNullObject(final UnpackerContext context,
            final Template<T> template, final int size) throws IOException {
        final ArrayList<Object> previous = context.previous;
        final int objID = previous.size();
        T result = template.preCreate(size);
        // Often null ...
        previous.add(result);
        result = template.readData(context, result, size);
        // in case preCreate() returns null
        previous.set(objID, result);
        return result;
    }

    /** Returns a previously read object.
     * @return */
    private static Object readPrevious(final Unpacker unpacker,
            final Template<?> template, final ArrayList<Object> previous)
            throws IOException {
        final int id = unpacker.readIndex();
        if (id >= previous.size()) {
            throw new IllegalStateException("Object with ID " + id
                    + " not read yet");
        }
        final Object result = previous.get(id);
        if (result == null) {
            throw new IllegalStateException("Object with ID " + id
                    + " not (fully?) read yet");
        }
        if ((template != null) && !template.accept(result)) {
            throw new IllegalStateException("Object with ID " + id
                    + " of type " + result.getClass()
                    + " not supported by template " + template);
        }
        return result;
    }

    /** Reads any 1D Object. We assume the template is valid. */
    private static <T> T[] readNewNonNull1DArray(final UnpackerContext context,
            final Template<T> template, final int size) throws IOException {
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) template.preCreateArray(1, size);
        context.previous.add(result);
        template.read1DArray(context, result, size);
        return result;
    }

    /** Reads any 2D Object. We assume the template is valid. */
    private static <T> T[][] readNewNonNull2DArray(
            final UnpackerContext context, final Template<T> template,
            final int size) throws IOException {
        @SuppressWarnings("unchecked")
        final T[][] result = (T[][]) template.preCreateArray(2, size);
        context.previous.add(result);
        template.read2DArray(context, result, size);
        return result;
    }

    /** Reads any 3D Object. We assume the template is valid. */
    private static <T> T[][][] readNewNonNull3DArray(
            final UnpackerContext context, final Template<T> template,
            final int size) throws IOException {
        @SuppressWarnings("unchecked")
        final T[][][] result = (T[][][]) template.preCreateArray(3, size);
        context.previous.add(result);
        template.read3DArray(context, result, size);
        return result;
    }

    /** Reads an "array" object. (Not to be confused with an array OF objects)
     * @return */
    private static Object readArrayObject(final UnpackerContext context,
            Template<?> template) throws IOException {
        final Unpacker unpacker = context.unpacker;
        final int size = unpacker.readArrayBegin();
        final int tidPlusDimension = unpacker.readIndex();
        final int tid = tidPlusDimension / 4;
        final int dimension = tidPlusDimension % 4;
        if (template != null) {
            if (tid != template.getID()) {
                throw new IllegalArgumentException("Expected: " + template
                        + " but was " + context.getTemplate(tid));
            }
        } else {
            template = context.getTemplate(tid);
        }
        if (!template.isListType()) {
            throw new IllegalStateException("Template " + template
                    + " does not support ARRAYs");
        }
        final Object result;
        if (dimension == 0) {
            result = readNewNonNullObject(context, template, size - 1);
        } else if (dimension == 1) {
            result = readNewNonNull1DArray(context, template, size - 1);
        } else if (dimension == 2) {
            result = readNewNonNull2DArray(context, template, size - 1);
        } else {
            result = readNewNonNull3DArray(context, template, size - 1);
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads an "map" object.
     * @return */
    @SuppressWarnings("unchecked")
    private static <T> T readMapObject(final UnpackerContext context,
            Template<T> template) throws IOException {
        final Unpacker unpacker = context.unpacker;
        final int size = unpacker.readMapBegin();
        final int tidPlusDimension = unpacker.readIndex();
        final int tid = tidPlusDimension / 4;
        if (template != null) {
            if (tid != template.getID()) {
                throw new IllegalArgumentException("Expected: " + template
                        + " but was " + context.getTemplate(tid));
            }
        } else {
            template = (Template<T>) context.getTemplate(tid);
        }
        if (template.isListType()) {
            throw new IllegalStateException("Template " + template
                    + " does not support MAPs");
        }
        final T result = readNewNonNullObject(context, template, size - 1);
        unpacker.readMapEnd();
        return result;
    }

    /** Reads any Object. Fails if Object type does not match template type. */
    public static Object readObject(final UnpackerContext context,
            final Template<?> template) throws IOException {
        final Unpacker unpacker = context.unpacker;
        final ArrayList<Object> previous = context.previous;
        final ValueType type = unpacker.getNextType();
        if (type == ValueType.NIL) {
            return null;
        }
        if (type == ValueType.INTEGER) {
            // id is "previous object ID"
            return readPrevious(unpacker, template, previous);
        }
        // New object
        if (type == ValueType.ARRAY) {
            return readArrayObject(context, template);
        }
        if (type == ValueType.MAP) {
            return readMapObject(context, template);
        }
        if (type == ValueType.RAW) {
            if (template == null) {
                // Must be string
                return readNewNonNullObject(context, BasicTemplates.STRING, -1);
            }
            return readNewNonNullObject(context, template, -1);
        }
        throw new IllegalStateException("Unexpected value type: " + type
                + " for template " + template);
    }

    /** Computes the array-depth of a class. */
    public static int getArrayDepth(Class<?> c) {
        int depth = 0;
        Class<?> cc;
        while (c.isArray() && !(cc = c.getComponentType()).isPrimitive()) {
            depth++;
            c = cc;
        }
        return depth;
    }

    /** Constructor. */
    protected AbstractTemplate(final int id, final Class<T> type,
            final boolean isListType, final boolean isMergeable) {
        this(id, type, isListType, isMergeable, -1);
    }

    /** Constructor. */
    @SuppressWarnings("unchecked")
    protected AbstractTemplate(final int id, final Class<T> type,
            final boolean isListType, final boolean isMergeable,
            final int fixedSize) {
        this.id = id;
        this.type = Objects.requireNonNull(type);
        type1D = (Class<T[]>) Array.newInstance(type, 0).getClass();
        type2D = (Class<T[][]>) Array.newInstance(type1D, 0).getClass();
        isFinalOrPrimitiveArray = CLASS_NAME_CONVERTER.isFinal(type);
        this.isListType = isListType;
        this.isMergeable = isMergeable;
        this.fixedSize = (fixedSize >= 0) ? fixedSize : -1;
    }

    /** toString */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ",type="
                + type.getName() + ")";
    }

    /** Returns the template ID. */
    @Override
    public final int getID() {
        return id;
    }

    /** Returns the type that is supported. */
    @Override
    public final Class<T> getType() {
        return type;
    }

    /** Returns true, if the template would support reading/writing objects of this type. */
    @Override
    public boolean accept(final Object o) {
        return (o == null) || (o.getClass() == type);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.Template#isFinalOrPrimitiveArray()
     */
    @Override
    public final boolean isFinalOrPrimitiveArray() {
        return isFinalOrPrimitiveArray;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.Template#isListType()
     */
    @Override
    public final boolean isListType() {
        return isListType;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.Template#isMergeable()
     */
    @Override
    public final boolean isMergeable() {
        return isMergeable;
    }

    /**
     * Returns a non-negative value, if this type has a "fixed size"
     *
     * @see getSpaceRequired()
     */
    @Override
    public final int getFixedSize() {
        return fixedSize;
    }

    /** Writes the type ID. */
    private void writeID(final Packer packer, final int dimensions)
            throws IOException {
        packer.writeIndex(4 * id + dimensions);
    }

    /** Writea an Object as a list/array. */
    private void writeList(final PackerContext context, final T v,
            final int size) throws IOException {
        final Packer packer = context.packer;
        packer.writeArrayBegin(size + 1);
        writeID(packer, 0);
        writeData(context, size, v);
        packer.writeArrayEnd(true);
    }

    /** Writes an Object as a map. */
    private void writeMap(final PackerContext context, final T v, final int size)
            throws IOException {
        final Packer packer = context.packer;
        packer.writeMapBegin(size + 1);
        // Map entry key
        writeID(packer, 0);
        // Map entry value
        writeMapHeaderValue(context, v, size);
        writeData(context, size, v);
        packer.writeMapEnd(true);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.Template#writeNonArrayObject(com.blockwithme.msgpack.templates.PackerContext, com.blockwithme.msgpack.MetaData, java.lang.Object)
     */
    @Override
    public final void writeNonArrayObject(final PackerContext context, final T v)
            throws IOException {
        if (v == null) {
            context.packer.writeNil();
        } else {
            final int size = getSpaceRequired(context, v);
            if (isListType) {
                writeList(context, v, size);
            } else {
                writeMap(context, v, size);
            }
        }
    }

    /** Final fixed-size non-null type write */
    private void finalFixedSizeNonNullWrite(final PackerContext context,
            final T[] v) throws IOException {
        final Packer packer = context.packer;
        packer.writeArrayBegin(v.length * fixedSize + 1);
        writeID(packer, 1);
        for (int i = 0; i < v.length; i++) {
            final T t = v[i];
            if (t == null) {
                throw new IllegalStateException("v[" + i + "] was null!");
            }
            writeData(context, fixedSize, t);
        }
        packer.writeArrayEnd(true);
    }

    /** Writes an array of anything */
    private void writeArrayDataAsRandomObjects(final PackerContext context,
            final Object[] v) throws IOException {
        final ObjectPacker op = context.objectPacker;
        for (int i = 0; i < v.length; i++) {
            op.writeObject(v[i]);
        }
    }

    /** Final type write */
    private void writeArrayAsMyObjects(final PackerContext context, final T[] v)
            throws IOException {
        final Packer packer = context.packer;
        packer.writeArrayBegin(v.length + 1);
        writeID(packer, 1);
        for (int i = 0; i < v.length; i++) {
            writeNonArrayObject(context, v[i]);
        }
        packer.writeArrayEnd(true);
    }

    /** Non-final type write */
    private void writeArrayAsRandomObjects(final PackerContext context,
            final T[] v) throws IOException {
        final Packer packer = context.packer;
        packer.writeArrayBegin(v.length + 1);
        writeID(packer, 1);
        writeArrayDataAsRandomObjects(context, v);
        packer.writeArrayEnd(true);
    }

    /** Writes an array of T */
    @Override
    public final void write1DArray(final PackerContext context, final T[] v,
            final boolean canContainNullValue) throws IOException {
        if (v == null) {
            context.packer.writeNil();
        } else {
            if (isFinalOrPrimitiveArray) {
                if ((fixedSize > 0) && !canContainNullValue) {
                    // A primitive array will not have a fixed size.
                    finalFixedSizeNonNullWrite(context, v);
                } else {
                    writeArrayAsMyObjects(context, v);
                }
            } else {
                writeArrayAsRandomObjects(context, v);
            }
        }
    }

    /** Writes a 2D array of T */
    @Override
    public final void write2DArray(final PackerContext context, final T[][] v,
            final boolean canContainNullValue) throws IOException {
        final Packer packer = context.packer;
        if (v == null) {
            packer.writeNil();
        } else {
            packer.writeArrayBegin(v.length + 1);
            writeID(packer, 2);
            if (isFinalOrPrimitiveArray) {
                for (int i = 0; i < v.length; i++) {
                    write1DArray(context, v[i], canContainNullValue);
                }
            } else {
                writeArrayDataAsRandomObjects(context, v);
            }
            packer.writeArrayEnd(true);
        }
    }

    /** Writes a 3D array of T */
    @Override
    public final void write3DArray(final PackerContext context,
            final T[][][] v, final boolean canContainNullValue)
            throws IOException {
        final Packer packer = context.packer;
        if (v == null) {
            packer.writeNil();
        } else {
            packer.writeArrayBegin(v.length + 1);
            writeID(packer, 3);
            if (isFinalOrPrimitiveArray) {
                for (int i = 0; i < v.length; i++) {
                    write2DArray(context, v[i], canContainNullValue);
                }
            } else {
                writeArrayDataAsRandomObjects(context, v);
            }
            packer.writeArrayEnd(true);
        }
    }

    /**
     * Creating and returning an empty instance before reading enable cycles in
     * the object graph. This method must not return null.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final T preCreateArray(final int arrayDepth, final int size) {
        if (arrayDepth == 1) {
            return (T) Array.newInstance(type, size);
        }
        if (arrayDepth == 2) {
            return (T) Array.newInstance(type1D, size);
        }
        if (arrayDepth == 3) {
            return (T) Array.newInstance(type2D, size);
        }
        throw new IllegalArgumentException("Unsupposted array depth: "
                + arrayDepth);
    }

    /** Reads an 1D array of T */
    @SuppressWarnings("unchecked")
    @Override
    public final void read1DArray(final UnpackerContext context,
            final T[] preCreated, final int size) throws IOException {
        final Template<T> template = isFinalOrPrimitiveArray ? this : null;
        for (int i = 0; i < size; i++) {
            preCreated[i] = (T) readObject(context, template);
        }
    }

    /** Reads an 2D array of T */
    @SuppressWarnings("unchecked")
    @Override
    public final void read2DArray(final UnpackerContext context,
            final T[][] preCreated, final int size) throws IOException {
        final Template<T> template = isFinalOrPrimitiveArray ? this : null;
        for (int i = 0; i < size; i++) {
            preCreated[i] = (T[]) readObject(context, template);
        }
    }

    /** Reads an 3D array of T */
    @SuppressWarnings("unchecked")
    @Override
    public final void read3DArray(final UnpackerContext context,
            final T[][][] preCreated, final int size) throws IOException {
        final Template<T> template = isFinalOrPrimitiveArray ? this : null;
        for (int i = 0; i < size; i++) {
            preCreated[i] = (T[][]) readObject(context, template);
        }
    }

    /** Writes the Map-Object header-entry value. Normally unused, and hence nil. */
    protected void writeMapHeaderValue(final PackerContext context, final T v,
            final int size) throws IOException {
        context.packer.writeNil();
    }

    @Override
    public int getSpaceRequired(final PackerContext context, final T v) {
        if (fixedSize >= 0) {
            return fixedSize;
        }
        throw new IllegalStateException("Must be implemented!");
    }

    @Override
    public T preCreate(final int size) {
        return null;
    }
}
