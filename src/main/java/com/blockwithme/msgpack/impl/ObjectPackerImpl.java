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
import com.blockwithme.msgpack.templates.AbstractTemplate;
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

    /** Writes an Object out. Object must be compatible with template. */
    @SuppressWarnings("unchecked")
    @Override
    public <E> ObjectPacker writeObject(final E o, Template<E> template)
            throws IOException {
        if (o == null) {
            if (context.required) {
                throw new IOException("Attempted to write null when required");
            }
            packer.writeNil();
            return this;
        }
        int depth = -1;
        if (template == null) {
            Class<?> c = o.getClass();
            depth = 0;
            Class<?> cc;
            while (c.isArray() && !(cc = c.getComponentType()).isPrimitive()) {
                depth++;
                c = cc;
            }
            template = context.getTemplate((Class<E>) c);
        }
        final int pos = tracker.track(o, template.isMergeable());
        if (pos == -1) {
            // New Object!
            if (depth == -1) {
                depth = AbstractTemplate.getArrayDepth(o.getClass());
            }
            if (depth == 0) {
                template.writeNonArrayObject(context, o);
            } else if (depth == 1) {
                template.write1DArray(context, (E[]) o, true);
            } else if (depth == 2) {
                template.write2DArray(context, (E[][]) o, true);
            } else if (depth == 3) {
                template.write3DArray(context, (E[][][]) o, true);
            } else {
                throw new IOException(
                        "Maximum non-primitive (+1 for primitives) array dimention is 3, but got "
                                + depth);
            }
            return this;
        }
        // Previous object
        packer.writeIndex(pos);
        return this;
    }

    /**
     * Writes an Object out. Template is determined based on object type.
     * Pass along a template for faster results.
     */
    @Override
    public <E> ObjectPacker writeObject(final E o) throws IOException {
        return writeObject(o, null);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.OwriteObject(acker#writeObject(java.lang.Class)
     */
    @Override
    public ObjectPacker writeObject(final Class<?> o) throws IOException {
        return writeObject(o, BasicTemplates.CLASS);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Boolean)
     */
    @Override
    public ObjectPacker writeObject(final Boolean o) throws IOException {
        return writeObject(o, BasicTemplates.BOOLEAN);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Byte)
     */
    @Override
    public ObjectPacker writeObject(final Byte o) throws IOException {
        return writeObject(o, BasicTemplates.BYTE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Short)
     */
    @Override
    public ObjectPacker writeObject(final Short o) throws IOException {
        return writeObject(o, BasicTemplates.SHORT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Character)
     */
    @Override
    public ObjectPacker writeObject(final Character o) throws IOException {
        return writeObject(o, BasicTemplates.CHARACTER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Integer)
     */
    @Override
    public ObjectPacker writeObject(final Integer o) throws IOException {
        return writeObject(o, BasicTemplates.INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Long)
     */
    @Override
    public ObjectPacker writeObject(final Long o) throws IOException {
        return writeObject(o, BasicTemplates.LONG);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Float)
     */
    @Override
    public ObjectPacker writeObject(final Float o) throws IOException {
        return writeObject(o, BasicTemplates.FLOAT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Double)
     */
    @Override
    public ObjectPacker writeObject(final Double o) throws IOException {
        return writeObject(o, BasicTemplates.DOUBLE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.math.BigInteger)
     */
    @Override
    public ObjectPacker writeObject(final BigInteger o) throws IOException {
        return writeObject(o, BasicTemplates.BIG_INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.math.BigDecimal)
     */
    @Override
    public ObjectPacker writeObject(final BigDecimal o) throws IOException {
        return writeObject(o, BasicTemplates.BIG_DECIMAL);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.String)
     */
    @Override
    public ObjectPacker writeObject(final String o) throws IOException {
        return writeObject(o, BasicTemplates.STRING);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(boolean[])
     */
    @Override
    public ObjectPacker writeObject(final boolean[] o) throws IOException {
        return writeObject(o, BasicTemplates.BOOLEAN_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(byte[])
     */
    @Override
    public ObjectPacker writeObject(final byte[] o) throws IOException {
        return writeObject(o, BasicTemplates.BYTE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(short[])
     */
    @Override
    public ObjectPacker writeObject(final short[] o) throws IOException {
        return writeObject(o, BasicTemplates.SHORT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(char[])
     */
    @Override
    public ObjectPacker writeObject(final char[] o) throws IOException {
        return writeObject(o, BasicTemplates.CHAR_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(int[])
     */
    @Override
    public ObjectPacker writeObject(final int[] o) throws IOException {
        return writeObject(o, BasicTemplates.INT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(long[])
     */
    @Override
    public ObjectPacker writeObject(final long[] o) throws IOException {
        return writeObject(o, BasicTemplates.LONG_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(float[])
     */
    @Override
    public ObjectPacker writeObject(final float[] o) throws IOException {
        return writeObject(o, BasicTemplates.FLOAT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(double[])
     */
    @Override
    public ObjectPacker writeObject(final double[] o) throws IOException {
        return writeObject(o, BasicTemplates.DOUBLE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.util.Date)
     */
    @Override
    public ObjectPacker writeObject(final Date o) throws IOException {
        return writeObject(o, BasicTemplates.DATE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.nio.ByteBuffer)
     */
    @Override
    public ObjectPacker writeObject(final ByteBuffer o) throws IOException {
        return writeObject(o, BasicTemplates.BYTE_BUFFER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(byte[], int, int)
     */
    @Override
    public ObjectPacker writeObject(final byte[] o, final int off, final int len)
            throws IOException {
        return writeObject(new ByteArraySlice(o, off, len),
                BasicTemplates.BYTE_ARRAY_SLICE);
    }
}
