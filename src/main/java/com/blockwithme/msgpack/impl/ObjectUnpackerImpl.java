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
package com.blockwithme.msgpack.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.blockwithme.msgpack.ObjectUnpacker;
import com.blockwithme.msgpack.Unpacker;
import com.blockwithme.msgpack.ValueType;
import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates.UnpackerContext;

/**
 * The ObjectUnpacker implementation.
 *
 * @author monster
 */
public class ObjectUnpackerImpl implements ObjectUnpacker {

    /** Tracks the previously returned objects. */
    private final ArrayList<Object> previous = new ArrayList<Object>(256);

    /** The real Unpacker */
    protected final Unpacker unpacker;

    /** The context */
    protected final UnpackerContext context;

    /**
     * Creates a new ObjectUnpackerImpl.
     * @param unpacker
     */
    public ObjectUnpackerImpl(final Unpacker unpacker,
            final UnpackerContext context) {
        this.unpacker = Objects.requireNonNull(unpacker);
        this.context = Objects.requireNonNull(context);
        context.unpacker = unpacker;
        context.objectUnpacker = this;
    }

    /** Returns the underlying unpacker, if any, otherwise self. */
    @Override
    public Unpacker unpacker() {
        return unpacker;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBoolean()
     */
    @Override
    public boolean readBoolean() throws IOException {
        return unpacker.readBoolean();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByte()
     */
    @Override
    public byte readByte() throws IOException {
        return unpacker.readByte();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShort()
     */
    @Override
    public short readShort() throws IOException {
        return unpacker.readShort();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readChar()
     */
    @Override
    public char readChar() throws IOException {
        return unpacker.readChar();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readInt()
     */
    @Override
    public int readInt() throws IOException {
        return unpacker.readInt();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLong()
     */
    @Override
    public long readLong() throws IOException {
        return unpacker.readLong();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloat()
     */
    @Override
    public float readFloat() throws IOException {
        return unpacker.readFloat();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDouble()
     */
    @Override
    public double readDouble() throws IOException {
        return unpacker.readDouble();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigInteger()
     */
    @Override
    public BigInteger readBigInteger() throws IOException {
        return readObject(BasicTemplates.BIG_INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigDecimal()
     */
    @Override
    public BigDecimal readBigDecimal() throws IOException {
        return readObject(BasicTemplates.BIG_DECIMAL);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBooleanArray()
     */
    @Override
    public boolean[] readBooleanArray() throws IOException {
        return readObject(BasicTemplates.BOOLEAN_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteArray()
     */
    @Override
    public byte[] readByteArray() throws IOException {
        return readObject(BasicTemplates.BYTE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShortArray()
     */
    @Override
    public short[] readShortArray() throws IOException {
        return readObject(BasicTemplates.SHORT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readCharArray()
     */
    @Override
    public char[] readCharArray() throws IOException {
        return readObject(BasicTemplates.CHAR_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readIntArray()
     */
    @Override
    public int[] readIntArray() throws IOException {
        return readObject(BasicTemplates.INT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLongArray()
     */
    @Override
    public long[] readLongArray() throws IOException {
        return readObject(BasicTemplates.LONG_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloatArray()
     */
    @Override
    public float[] readFloatArray() throws IOException {
        return readObject(BasicTemplates.FLOAT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDoubleArray()
     */
    @Override
    public double[] readDoubleArray() throws IOException {
        return readObject(BasicTemplates.DOUBLE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBooleanArrayArray()
     */
    @Override
    public boolean[][] readBooleanArrayArray() throws IOException {
        return readObject(BasicTemplates.BOOLEAN_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteArrayArray()
     */
    @Override
    public byte[][] readByteArrayArray() throws IOException {
        return readObject(BasicTemplates.BYTE_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShortArrayArray()
     */
    @Override
    public short[][] readShortArrayArray() throws IOException {
        return readObject(BasicTemplates.SHORT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readCharArrayArray()
     */
    @Override
    public char[][] readCharArrayArray() throws IOException {
        return readObject(BasicTemplates.CHAR_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readIntArrayArray()
     */
    @Override
    public int[][] readIntArrayArray() throws IOException {
        return readObject(BasicTemplates.INT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLongArrayArray()
     */
    @Override
    public long[][] readLongArrayArray() throws IOException {
        return readObject(BasicTemplates.LONG_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloatArrayArray()
     */
    @Override
    public float[][] readFloatArrayArray() throws IOException {
        return readObject(BasicTemplates.FLOAT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDoubleArrayArray()
     */
    @Override
    public double[][] readDoubleArrayArray() throws IOException {
        return readObject(BasicTemplates.DOUBLE_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readString()
     */
    @Override
    public String readString() throws IOException {
        return readObject(BasicTemplates.STRING);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteBuffer()
     */
    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        return readObject(BasicTemplates.BYTE_BUFFER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectUnpacker#readDate()
     */
    @Override
    public Date readDate() throws IOException {
        return readObject(BasicTemplates.DATE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectUnpacker#readEnum()
     */
    @Override
    public Enum<?> readEnum() throws IOException {
        return readObject(BasicTemplates.ENUM);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#getNextType()
     */
    @Override
    public ValueType getNextType() throws IOException {
        return unpacker.getNextType();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setRawSizeLimit(int)
     */
    @Override
    public void setRawSizeLimit(final int size) {
        unpacker.setRawSizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setArraySizeLimit(int)
     */
    @Override
    public void setArraySizeLimit(final int size) {
        unpacker.setArraySizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#setMapSizeLimit(int)
     */
    @Override
    public void setMapSizeLimit(final int size) {
        unpacker.setMapSizeLimit(size);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#skip()
     */
    @Override
    public void skip() throws IOException {
        unpacker.skip();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayBegin()
     */
    @Override
    public int readArrayBegin() throws IOException {
        return unpacker.readArrayBegin();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayEnd(boolean)
     */
    @Override
    public void readArrayEnd(final boolean check) throws IOException {
        unpacker.readArrayEnd(check);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readArrayEnd()
     */
    @Override
    public void readArrayEnd() throws IOException {
        unpacker.readArrayEnd();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapBegin()
     */
    @Override
    public int readMapBegin() throws IOException {
        return unpacker.readMapBegin();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapEnd(boolean)
     */
    @Override
    public void readMapEnd(final boolean check) throws IOException {
        unpacker.readMapEnd(check);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readMapEnd()
     */
    @Override
    public void readMapEnd() throws IOException {
        unpacker.readMapEnd();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readNil()
     */
    @Override
    public void readNil() throws IOException {
        unpacker.readNil();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#trySkipNil()
     */
    @Override
    public boolean trySkipNil() throws IOException {
        return unpacker.trySkipNil();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        unpacker.close();
    }

    /** Reads an index written with Packer.writeIndex(int). */
    @Override
    public int readIndex() throws IOException {
        return unpacker.readIndex();
    }

    @Override
    public Class<?> readClass() throws IOException {
        return readObject(BasicTemplates.CLASS);
    }

    /** Reads any Object. */
    @Override
    public Object readObject() throws IOException {
        final int id = unpacker.readInt();
        if (id == BasicTemplates.NULL_ID) {
            return null;
        }
        if (id > 0) {
            // New object; id is object type/template ID
            return readNewNonNullObject(context.getTemplate(id));
        }
        // id is "previous object ID"
        final Object result = previous.get(-id);
        if (result == null) {
            throw new IllegalStateException("Object with ID " + id
                    + " not (fully?) read yet");
        }
        return result;
    }

    /** Reads any Object. Fails if Object type does not match template type. */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(final Template<T> template) throws IOException {
        // Intentional NPE
        final int tid = template.getID();
        if (template != context.getTemplate(tid)) {
            throw new IllegalArgumentException("Unknown template: " + template);
        }
        final int id = unpacker.readInt();
        if (id == BasicTemplates.NULL_ID) {
            return null;
        }
        if (id > 0) {
            if (tid != id) {
                throw new IllegalArgumentException("Expected: " + template
                        + " buit was " + context.getTemplate(id));
            }
            // New object
            return readNewNonNullObject(template);
        }
        // id is "previous object ID"
        final Object result = previous.get(-id);
        if (result == null) {
            throw new IllegalStateException("Object with ID " + id
                    + " not (fully?) read yet");
        }
        return (T) result;
    }

    /**
     * Reads any Object.
     *
     * Object must be compatible with non-null template, but it is not checked.
     * Object must not be null, but it is not checked.
     * Object must not be reused anywhere else, but it is not checked.
     * No ID is read.
     *
     * Fastest, but least safe.
     */
    @Override
    public <T> T readUnsharedUncheckedNonNullNoID(final Template<T> template)
            throws IOException {
        return template.read(context);
    }

    /** Reads any Object. We assume the template is valid. */
    protected <T> T readNewNonNullObject(final Template<T> template)
            throws IOException {
        final int objID = previous.size() + 1;
        final T result = template.read(context);
        while (previous.size() <= objID) {
            previous.add(null);
        }
        previous.set(objID, result);
        return result;
    }
}
