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
package com.blockwithme.util;

import java.util.Arrays;

/**
 * A generic object that can contain anything.
 *
 * Primitive values are stored efficiently, without creating new objects/wrappers.
 *
 * But long and double require *two* "slots to be stored, so the first slot
 * after a long/double should not be used.
 *
 * Internally, new arrays are always allocated using a power of two, to reduce
 * temporary object creation, in the case of growth.
 *
 * Since the arrays could be reused, and are only used for temporary storage
 * (or at least, that is what I plain to use it for), I don't try to save the
 * last bit of RAM, but rather aims for fast read/write speed. This means for
 * example that a boolean takes as much room as an int.
 *
 * Using the built-in indexes, it is also possible to let the generic object
 * manage the position in the arrays itself.
 *
 * @author monster
 */
public class GenericObject {
    public static final Object[] NO_OBJECTS = new Object[0];
    public static final int[] NO_DATA = new int[0];
    private static final int MIN_ARRAY_SIZE = 8;

    /** The object values. Never null. */
    private Object[] objects;

    /** The data values. Never null. */
    private int[] data;

    /** The object array index. */
    private int objectIndex;

    /** The data array index. */
    private int dataIndex;

    /** Returns a power of two, bigger or equal to the input. */
    private static int powerOfTwo(final int i) {
        // Don't create too small arrays
        if (i < MIN_ARRAY_SIZE) {
            return MIN_ARRAY_SIZE;
        }
        // If already power of two, then we are done
        if (2 * i == (i ^ (i - 1) + 1)) {
            return i;
        }
        // Not power of two, so "round up" by moving highest bit one notch up
        return 1 << (Integer.highestOneBit(i) + 1);
    }

    /** Creates a default generic object. */
    public GenericObject() {
        objects = NO_OBJECTS;
        data = NO_DATA;
    }

    /**
     * Creates a generic object with the given arrays.
     *
     * Note that on growth of the arrays, the original array can then not be
     * used anymore.
     *
     * Remember that every long and double value requires *two* data slots.
     *
     * Null arrays are allowed, but not recommended. Use the empty array
     * constants instead.
     *
     * @param objects the array containing the objects, if any
     * @param data the array containing the primitive data, if any
     */
    public GenericObject(final Object[] objects, final int[] data) {
        this.objects = objects;
        this.data = data;
    }

    /**
     * Creates a generic object with the given capacities.
     *
     * Remember that every long and double value requires *two* data slots.
     *
     * @param minObjectsCapacity The minimum size for the object array. Actual size might be bigger.
     * @param minDataCapacity The minimum size for the data array. Actual size might be bigger.
     */
    public GenericObject(final int minObjectsCapacity, final int minDataCapacity) {
        this.objects = (minObjectsCapacity <= 0) ? NO_OBJECTS
                : new Object[powerOfTwo(minObjectsCapacity)];
        this.data = (minDataCapacity <= 0) ? NO_DATA
                : new int[powerOfTwo(minDataCapacity)];
    }

    /** Returns the current objects capacity. */
    public final int getObjectsCapacity() {
        return (objects == null) ? 0 : objects.length;
    }

    /**
     * Returns the current data capacity.
     *
     * Remember that every long and double value requires *two* data slots.
     */
    public final int getDataCapacity() {
        return (data == null) ? 0 : data.length;
    }

    /** Ensures the minimum total object capacity. */
    public final GenericObject ensureTotalObjectCapacity(
            final int minObjectsCapacity) {
        final int oldCapacity = getObjectsCapacity();
        if (minObjectsCapacity > oldCapacity) {
            final int capacity = powerOfTwo(minObjectsCapacity);
            if (oldCapacity == 0) {
                objects = new Object[capacity];
            } else {
                objects = Arrays.copyOf(objects, capacity);
            }
        }
        return this;
    }

    /**
     * Ensures the minimum total data capacity.
     *
     * Remember that every long and double values requires *two* data slots.
     */
    public final GenericObject ensureTotalDataCapacity(final int minDataCapacity) {
        final int oldCapacity = getDataCapacity();
        if (minDataCapacity > oldCapacity) {
            final int newCapacity = powerOfTwo(minDataCapacity);
            if (oldCapacity == 0) {
                data = new int[newCapacity];
            } else {
                data = Arrays.copyOf(data, newCapacity);
            }
        }
        return this;
    }

    /** Ensures the minimum *free* object capacity. */
    public final GenericObject ensureFreeObjectCapacity(final int increment) {
        return ensureTotalObjectCapacity(objectIndex + increment);
    }

    /**
     * Ensures the minimum *free* data capacity.
     *
     * Remember that every long and double values requires *two* data slots.
     */
    public final GenericObject ensureFreeDataCapacity(final int increment) {
        return ensureTotalDataCapacity(dataIndex + increment);
    }

    /** Returns the current underlying object array. */
    public final Object[] getObjects() {
        return objects;
    }

    /**
     * Sets the current underlying object array.
     *
     * Null arrays are allowed, but not recommended. Use the empty array
     * constants instead.
     */
    public final GenericObject setObjects(final Object[] newObjects) {
        objects = newObjects;
        return this;
    }

    /** Returns the current underlying data array. */
    public final int[] getData() {
        return data;
    }

    /**
     * Sets the current underlying data array.
     *
     * Null arrays are allowed, but not recommended. Use the empty array
     * constants instead.
     */
    public final GenericObject setData(final int[] newData) {
        data = newData;
        return this;
    }

    /** Returns the object array index. */
    public final int getObjectIndex() {
        return objectIndex;
    }

    /**
     * Sets the object array index.
     *
     * @param newObjectIndex the new object array index. Not validation performed!
     */
    public final GenericObject setObjectIndex(final int newObjectIndex) {
        objectIndex = newObjectIndex;
        return this;
    }

    /** Returns the data array index. */
    public final int getDataIndex() {
        return dataIndex;
    }

    /**
     * Sets the data array index.
     *
     * @param newObjectIndex the new object array index. Not validation performed!
     */
    public final GenericObject setDataIndex(final int newDataIndex) {
        dataIndex = newDataIndex;
        return this;
    }

    /**
     * Return the boolean at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final boolean getBoolean(final int dataIndex) {
        return data[dataIndex] != 0;
    }

    /**
     * Sets the boolean at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setBoolean(final int dataIndex,
            final boolean value) {
        data[dataIndex] = value ? 1 : 0;
        return this;
    }

    /**
     * Return the byte at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final byte getByte(final int dataIndex) {
        return (byte) data[dataIndex];
    }

    /**
     * Sets the byte at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setByte(final int dataIndex, final byte value) {
        data[dataIndex] = value;
        return this;
    }

    /**
     * Return the short at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final short getShort(final int dataIndex) {
        return (short) data[dataIndex];
    }

    /**
     * Sets the short at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setShort(final int dataIndex, final short value) {
        data[dataIndex] = value;
        return this;
    }

    /**
     * Return the char at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final char getChar(final int dataIndex) {
        return (char) data[dataIndex];
    }

    /**
     * Sets the char at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setChar(final int dataIndex, final char value) {
        data[dataIndex] = value;
        return this;
    }

    /**
     * Return the int at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final int getInt(final int dataIndex) {
        return data[dataIndex];
    }

    /**
     * Sets the int at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setInt(final int dataIndex, final int value) {
        data[dataIndex] = value;
        return this;
    }

    /**
     * Return the float at the given *data* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final float getFloat(final int dataIndex) {
        return Float.intBitsToFloat(data[dataIndex]);
    }

    /**
     * Sets the float at the given *data* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setFloat(final int dataIndex, final float value) {
        data[dataIndex] = Float.floatToRawIntBits(value);
        return this;
    }

    /**
     * Return the long at the given *data* index.
     *
     * Note that longs take up *two* indexes.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final long getLong(final int dataIndex) {
        final int low = data[dataIndex];
        final int high = data[dataIndex + 1];
        return ((((long) high) << 32L) & 0xFFFFFFFF00000000L)
                | ((low) & 0xFFFFFFFFL);
    }

    /**
     * Sets the long at the given *data* index.
     *
     * Note that longs take up *two* indexes.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setLong(final int dataIndex, final long value) {
        data[dataIndex] = (int) value;
        data[dataIndex + 1] = (int) (value >> 32L);
        return this;
    }

    /**
     * Return the double at the given *data* index.
     *
     * Note that doubles take up *two* indexes.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final double getDouble(final int dataIndex) {
        return Double.longBitsToDouble(getLong(dataIndex));
    }

    /**
     * Sets the double at the given *data* index.
     *
     * Note that doubles take up *two* indexes.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setDouble(final int dataIndex, final double value) {
        return setLong(dataIndex, Double.doubleToRawLongBits(value));
    }

    /**
     * Return the object at the given *object* index.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored object.
     *
     * @throws java.lang.NullPointerException if objects is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    @SuppressWarnings("unchecked")
    public final <E> E getObject(final int objectIndex) {
        return (E) objects[objectIndex];
    }

    /**
     * Sets the object at the given *object* index.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if objects is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setObject(final int objectIndex,
            final Object value) {
        objects[objectIndex] = value;
        return this;
    }

    /**
     * Return the next boolean.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final boolean getBoolean() {
        return getBoolean(dataIndex++);
    }

    /**
     * Sets the next boolean.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setBoolean(final boolean value) {
        return setBoolean(dataIndex++, value);
    }

    /**
     * Sets the next boolean. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setBooleanSafe(final boolean value) {
        ensureFreeDataCapacity(1);
        return setBoolean(value);
    }

    /**
     * Return the next byte.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final byte getByte() {
        return getByte(dataIndex++);
    }

    /**
     * Sets the next byte.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setByte(final byte value) {
        return setByte(dataIndex++, value);
    }

    /**
     * Sets the next byte. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setByteSafe(final byte value) {
        ensureFreeDataCapacity(1);
        return setByte(value);
    }

    /**
     * Return the next short.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final short getShort() {
        return getShort(dataIndex++);
    }

    /**
     * Sets the next short.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setShort(final short value) {
        return setShort(dataIndex++, value);
    }

    /**
     * Sets the next short. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setShortSafe(final short value) {
        ensureFreeDataCapacity(1);
        return setShort(value);
    }

    /**
     * Return the next char.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final char getChar() {
        return getChar(dataIndex++);
    }

    /**
     * Sets the next char.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setChar(final char value) {
        return setChar(dataIndex++, value);
    }

    /**
     * Sets the next char. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setCharSafe(final char value) {
        ensureFreeDataCapacity(1);
        return setChar(value);
    }

    /**
     * Return the next int.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final int getInt() {
        return getInt(dataIndex++);
    }

    /**
     * Sets the next int.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setInt(final int value) {
        return setInt(dataIndex++, value);
    }

    /**
     * Sets the next int. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setIntSafe(final int value) {
        ensureFreeDataCapacity(1);
        return setInt(value);
    }

    /**
     * Return the next float.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final float getFloat() {
        return getFloat(dataIndex++);
    }

    /**
     * Sets the next float.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setFloat(final float value) {
        return setFloat(dataIndex++, value);
    }

    /**
     * Sets the next float. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setFloatSafe(final float value) {
        ensureFreeDataCapacity(1);
        return setFloat(value);
    }

    /**
     * Return the next long.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final long getLong() {
        final long result = getLong(dataIndex);
        dataIndex += 2;
        return result;
    }

    /**
     * Sets the next long.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setLong(final long value) {
        setLong(dataIndex, value);
        dataIndex += 2;
        return this;
    }

    /**
     * Sets the next long. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setLongSafe(final long value) {
        ensureFreeDataCapacity(2);
        return setLong(value);
    }

    /**
     * Return the next double.
     *
     * Note that there is no validation against the type of the currently stored data.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final double getDouble() {
        final double result = getDouble(dataIndex);
        dataIndex += 2;
        return result;
    }

    /**
     * Sets the next double.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setDouble(final double value) {
        setDouble(dataIndex, value);
        dataIndex += 2;
        return this;
    }

    /**
     * Sets the next double. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if data is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setDoubleSafe(final double value) {
        ensureFreeDataCapacity(2);
        return setDouble(value);
    }

    /**
     * Return the next object.
     *
     * Note that the data and object index are independent. Also note that
     * there is no validation against the type of the currently stored object.
     *
     * @throws java.lang.NullPointerException if objects is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final <E> E getObject() {
        return getObject(objectIndex++);
    }

    /**
     * Sets the next object.
     *
     * Note that the data and object index are independent.
     *
     * @throws java.lang.NullPointerException if objects is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setObject(final Object value) {
        return setObject(objectIndex++, value);
    }

    /**
     * Sets the next object. Grows the array if needed.
     *
     * @throws java.lang.NullPointerException if objects is null
     * @throws java.lang.ArrayIndexOutOfBoundsException if index outside valid range
     */
    public final GenericObject setObjectSafe(final Object value) {
        ensureFreeObjectCapacity(1);
        return setObject(objectIndex++, value);
    }
}
