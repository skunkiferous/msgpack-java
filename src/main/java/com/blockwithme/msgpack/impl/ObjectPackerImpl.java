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

import com.blockwithme.msgpack.ObjectPacker;
import com.blockwithme.msgpack.Packer;
import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.util.ObjectTracker;

/**
 * ObjectPacker implementation.
 *
 * @author monster
 */
public class ObjectPackerImpl implements ObjectPacker {

    /** The real Packer. */
    protected final Packer packer;

    /** The context */
    protected final PackerContext context;

    /** The ObjectTracker */
    private final ObjectTracker tracker = new ObjectTracker();

    /**
     * @param packer
     */
    public ObjectPackerImpl(final Packer packer, final PackerContext context) {
        this.packer = Objects.requireNonNull(packer);
        this.context = Objects.requireNonNull(context);
        context.packer = packer;
        context.objectPacker = this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#packer()
     */
    @Override
    public Packer packer() {
        return packer;
    }

    /**
     * Writes an Object out. Template is determined based on object type.
     * Pass along a template for faster results.
     */
    @Override
    public <E> ObjectPacker write(final E o) throws IOException {
        if (o == null) {
            if (context.required) {
                throw new IOException("Attempted to write null when required");
            }
            packer.write(BasicTemplates.NULL_ID);
            return this;
        }
        final int pos = tracker.track(o);
        if (pos == 0) {
            // New Object!
            @SuppressWarnings("unchecked")
            final Class<E> cls = (Class<E>) o.getClass();
            context.getTemplate(cls).write(context, o);
            return this;
        }
        // Previous object
        packer.writeIndex(-pos);
        return this;
    }

    /** Writes an Object out. Object must be compatible with template. */
    @Override
    public <E> ObjectPacker write(final E o, final Template<E> template)
            throws IOException {
        if (o == null) {
            if (context.required) {
                throw new IOException("Attempted to write null when required");
            }
            packer.write(BasicTemplates.NULL_ID);
            return this;
        }
        final int pos = tracker.track(o);
        if (pos == 0) {
            // New Object!
            template.checkAndWrite(context, o);
            return this;
        }
        // Previous object
        packer.writeIndex(-pos);
        return this;
    }

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
    @Override
    public <E> ObjectPacker writeUnsharedUncheckedNonNullNoID(final E o,
            final Template<E> template) throws IOException {
        // New Object! Unshared! Non-Null! Of-Correct-Type!
        template.writeNoID(context, o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Class)
     */
    @Override
    public ObjectPacker write(final Class<?> o) throws IOException {
        return write(o, BasicTemplates.CLASS);
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        packer.close();
    }

    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
        packer.flush();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(boolean)
     */
    @Override
    public ObjectPacker write(final boolean o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(byte)
     */
    @Override
    public ObjectPacker write(final byte o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(short)
     */
    @Override
    public ObjectPacker write(final short o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(char)
     */
    @Override
    public ObjectPacker write(final char o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(int)
     */
    @Override
    public ObjectPacker write(final int o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(long)
     */
    @Override
    public ObjectPacker write(final long o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(float)
     */
    @Override
    public ObjectPacker write(final float o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(double)
     */
    @Override
    public ObjectPacker write(final double o) throws IOException {
        packer.write(o);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(boolean[])
     */
    @Override
    public ObjectPacker write(final boolean[] o) throws IOException {
        return write(o, BasicTemplates.BOOLEAN_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(byte[])
     */
    @Override
    public ObjectPacker write(final byte[] o) throws IOException {
        return write(o, BasicTemplates.BYTE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(short[])
     */
    @Override
    public ObjectPacker write(final short[] o) throws IOException {
        return write(o, BasicTemplates.SHORT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(char[])
     */
    @Override
    public ObjectPacker write(final char[] o) throws IOException {
        return write(o, BasicTemplates.CHAR_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(int[])
     */
    @Override
    public ObjectPacker write(final int[] o) throws IOException {
        return write(o, BasicTemplates.INT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(long[])
     */
    @Override
    public ObjectPacker write(final long[] o) throws IOException {
        return write(o, BasicTemplates.LONG_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(float[])
     */
    @Override
    public ObjectPacker write(final float[] o) throws IOException {
        return write(o, BasicTemplates.FLOAT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(double[])
     */
    @Override
    public ObjectPacker write(final double[] o) throws IOException {
        return write(o, BasicTemplates.DOUBLE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(boolean[][])
     */
    @Override
    public ObjectPacker write(final boolean[][] o) throws IOException {
        return write(o, BasicTemplates.BOOLEAN_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(byte[][])
     */
    @Override
    public ObjectPacker write(final byte[][] o) throws IOException {
        return write(o, BasicTemplates.BYTE_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(short[][])
     */
    @Override
    public ObjectPacker write(final short[][] o) throws IOException {
        return write(o, BasicTemplates.SHORT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(char[][])
     */
    @Override
    public ObjectPacker write(final char[][] o) throws IOException {
        return write(o, BasicTemplates.CHAR_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(int[][])
     */
    @Override
    public ObjectPacker write(final int[][] o) throws IOException {
        return write(o, BasicTemplates.INT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(long[][])
     */
    @Override
    public ObjectPacker write(final long[][] o) throws IOException {
        return write(o, BasicTemplates.LONG_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(float[][])
     */
    @Override
    public ObjectPacker write(final float[][] o) throws IOException {
        return write(o, BasicTemplates.FLOAT_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(double[][])
     */
    @Override
    public ObjectPacker write(final double[][] o) throws IOException {
        return write(o, BasicTemplates.DOUBLE_ARRAY_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Boolean)
     */
    @Override
    public ObjectPacker write(final Boolean o) throws IOException {
        return write(o, BasicTemplates.BOOLEAN);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Byte)
     */
    @Override
    public ObjectPacker write(final Byte o) throws IOException {
        return write(o, BasicTemplates.BYTE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Short)
     */
    @Override
    public ObjectPacker write(final Short o) throws IOException {
        return write(o, BasicTemplates.SHORT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Character)
     */
    @Override
    public ObjectPacker write(final Character o) throws IOException {
        return write(o, BasicTemplates.CHARACTER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Integer)
     */
    @Override
    public ObjectPacker write(final Integer o) throws IOException {
        return write(o, BasicTemplates.INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Long)
     */
    @Override
    public ObjectPacker write(final Long o) throws IOException {
        return write(o, BasicTemplates.LONG);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Float)
     */
    @Override
    public ObjectPacker write(final Float o) throws IOException {
        return write(o, BasicTemplates.FLOAT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Double)
     */
    @Override
    public ObjectPacker write(final Double o) throws IOException {
        return write(o, BasicTemplates.DOUBLE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.math.BigInteger)
     */
    @Override
    public ObjectPacker write(final BigInteger o) throws IOException {
        return write(o, BasicTemplates.BIG_INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.math.BigDecimal)
     */
    @Override
    public ObjectPacker write(final BigDecimal o) throws IOException {
        return write(o, BasicTemplates.BIG_DECIMAL);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.String)
     */
    @Override
    public ObjectPacker write(final String o) throws IOException {
        return write(o, BasicTemplates.STRING);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(byte[], int, int)
     */
    @Override
    public ObjectPacker write(final byte[] o, final int off, final int len)
            throws IOException {
        packer.write(o, off, len);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.nio.ByteBuffer)
     */
    @Override
    public ObjectPacker write(final ByteBuffer o) throws IOException {
        return write(o, BasicTemplates.BYTE_BUFFER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeNil()
     */
    @Override
    public ObjectPacker writeNil() throws IOException {
        packer.writeNil();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeArrayBegin(int)
     */
    @Override
    public ObjectPacker writeArrayBegin(final int size) throws IOException {
        packer.writeArrayBegin(size);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeArrayEnd(boolean)
     */
    @Override
    public ObjectPacker writeArrayEnd(final boolean check) throws IOException {
        packer.writeArrayEnd(check);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeArrayEnd()
     */
    @Override
    public ObjectPacker writeArrayEnd() throws IOException {
        packer.writeArrayEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeMapBegin(int)
     */
    @Override
    public ObjectPacker writeMapBegin(final int size) throws IOException {
        packer.writeMapBegin(size);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeMapEnd(boolean)
     */
    @Override
    public ObjectPacker writeMapEnd(final boolean check) throws IOException {
        packer.writeMapEnd(check);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeMapEnd()
     */
    @Override
    public ObjectPacker writeMapEnd() throws IOException {
        packer.writeMapEnd();
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeIndex(int)
     */
    @Override
    public ObjectPacker writeIndex(final int index) throws IOException {
        packer.writeIndex(index);
        return this;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Object[])
     */
    @Override
    public ObjectPacker write(final Object[] o) throws IOException {
        return write(o, BasicTemplates.OBJECT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.util.Date)
     */
    @Override
    public ObjectPacker write(final Date o) throws IOException {
        return write(o, BasicTemplates.DATE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#write(java.lang.Enum)
     */
    @Override
    public ObjectPacker write(final Enum<?> o) throws IOException {
        return write(o, BasicTemplates.ENUM);
    }
}
