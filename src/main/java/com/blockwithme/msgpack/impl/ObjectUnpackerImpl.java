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
import java.util.Date;
import java.util.Objects;

import com.blockwithme.msgpack.ObjectUnpacker;
import com.blockwithme.msgpack.PostDeserListener;
import com.blockwithme.msgpack.Unpacker;
import com.blockwithme.msgpack.templates.AbstractTemplate;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates.UnpackerContext;

/**
 * The ObjectUnpacker implementation. All the hard work happens in:
 *
 * AbstractTemplate.readObject(UnpackerContext, Template);
 *
 * @author monster
 */
public class ObjectUnpackerImpl implements ObjectUnpacker {

    /** The real Unpacker */
    protected final Unpacker unpacker;

    /** The context */
    protected final UnpackerContext context;

    /**
     * Creates a new ObjectUnpackerImpl.
     * @param unpacker
     * @throws IOException
     */
    public ObjectUnpackerImpl(final Unpacker unpacker,
            final UnpackerContext context) throws IOException {
        this.unpacker = Objects.requireNonNull(unpacker);
        this.context = Objects.requireNonNull(context);
        context.unpacker = unpacker;
        context.objectUnpacker = this;
        context.format = unpacker.readIndex();
        context.schema = unpacker.readIndex();
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
    public Boolean readBoolean() throws IOException {
        return (Boolean) readObject(context.basicTemplates.BOOLEAN);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByte()
     */
    @Override
    public Byte readByte() throws IOException {
        return (Byte) readObject(context.basicTemplates.BYTE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShort()
     */
    @Override
    public Short readShort() throws IOException {
        return (Short) readObject(context.basicTemplates.SHORT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readChar()
     */
    @Override
    public Character readChar() throws IOException {
        return (Character) readObject(context.basicTemplates.CHARACTER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readInt()
     */
    @Override
    public Integer readInt() throws IOException {
        return (Integer) readObject(context.basicTemplates.INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLong()
     */
    @Override
    public Long readLong() throws IOException {
        return (Long) readObject(context.basicTemplates.LONG);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloat()
     */
    @Override
    public Float readFloat() throws IOException {
        return (Float) readObject(context.basicTemplates.FLOAT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDouble()
     */
    @Override
    public Double readDouble() throws IOException {
        return (Double) readObject(context.basicTemplates.DOUBLE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigInteger()
     */
    @Override
    public BigInteger readBigInteger() throws IOException {
        return (BigInteger) readObject(context.basicTemplates.BIG_INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBigDecimal()
     */
    @Override
    public BigDecimal readBigDecimal() throws IOException {
        return (BigDecimal) readObject(context.basicTemplates.BIG_DECIMAL);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readBooleanArray()
     */
    @Override
    public boolean[] readBooleanArray() throws IOException {
        return (boolean[]) readObject(context.basicTemplates.BOOLEAN_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteArray()
     */
    @Override
    public byte[] readByteArray() throws IOException {
        return (byte[]) readObject(context.basicTemplates.BYTE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readShortArray()
     */
    @Override
    public short[] readShortArray() throws IOException {
        return (short[]) readObject(context.basicTemplates.SHORT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readCharArray()
     */
    @Override
    public char[] readCharArray() throws IOException {
        return (char[]) readObject(context.basicTemplates.CHAR_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readIntArray()
     */
    @Override
    public int[] readIntArray() throws IOException {
        return (int[]) readObject(context.basicTemplates.INT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readLongArray()
     */
    @Override
    public long[] readLongArray() throws IOException {
        return (long[]) readObject(context.basicTemplates.LONG_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readFloatArray()
     */
    @Override
    public float[] readFloatArray() throws IOException {
        return (float[]) readObject(context.basicTemplates.FLOAT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readDoubleArray()
     */
    @Override
    public double[] readDoubleArray() throws IOException {
        return (double[]) readObject(context.basicTemplates.DOUBLE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readString()
     */
    @Override
    public String readString() throws IOException {
        return (String) readObject(context.basicTemplates.STRING);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.Unpacker#readByteBuffer()
     */
    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        return (ByteBuffer) readObject(context.basicTemplates.BYTE_BUFFER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectUnpacker#readDate()
     */
    @Override
    public Date readDate() throws IOException {
        return (Date) readObject(context.basicTemplates.DATE);
    }

    @Override
    public Class<?> readClass() throws IOException {
        return (Class<?>) readObject(context.basicTemplates.CLASS);
    }

    /** Reads any Object. */
    @Override
    public Object readObject() throws IOException {
        return AbstractTemplate.readObject(context, null, true);
    }

    /** Reads any Object. Fails if Object type does not match template type. */
    @Override
    public Object readObject(final Template<?> template) throws IOException {
        return AbstractTemplate.readObject(context, template, true);
    }

    /** Reads any Object. */
    @Override
    public Object readObject(final boolean ifObjectArrayCanContainNullValue)
            throws IOException {
        return AbstractTemplate.readObject(context, null,
                ifObjectArrayCanContainNullValue);
    }

    /** Reads any Object. Fails if Object type does not match template type. */
    @Override
    public Object readObject(final Template<?> template,
            final boolean ifObjectArrayCanContainNullValue) throws IOException {
        return AbstractTemplate.readObject(context, template,
                ifObjectArrayCanContainNullValue);
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        for (final Object o : context.previous) {
            if (o instanceof PostDeserListener) {
                final PostDeserListener pd = (PostDeserListener) o;
                pd.postDeser(context);
            }
        }
    }
}
