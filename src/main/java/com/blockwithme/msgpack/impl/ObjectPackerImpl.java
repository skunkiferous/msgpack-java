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
import com.blockwithme.msgpack.schema.Schema;
import com.blockwithme.msgpack.templates.AbstractTemplate;
import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;

/**
 * ObjectPacker implementation. All the hard work is done in:
 *
 * AbstractTemplate.writeObject(PackerContext, Object, Template);
 *
 * @author monster
 */
public class ObjectPackerImpl implements ObjectPacker {

    /** The real Packer. */
    protected final Packer packer;

    /** The context */
    protected final PackerContext context;

    /** The BasicTemplates */
    protected final BasicTemplates basicTemplates;

    /**
     * Creates an ObjectPackerImpl
     *
     * @param packer
     * @throws IOException
     */
    public ObjectPackerImpl(final Packer packer, final PackerContext context)
            throws IOException {
        this.packer = Objects.requireNonNull(packer);
        this.context = Objects.requireNonNull(context);
        context.packer = packer;
        context.objectPacker = this;
        final Schema schema = context.getSchema();
        basicTemplates = schema.basicTemplates;
        packer.writeIndex(schema.format);
        packer.writeIndex(schema.schema);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#packer()
     */
    @Override
    public Packer packer() {
        return packer;
    }

    /** Writes an Object out. Object must be compatible with template. */
    @Override
    public ObjectPacker writeObject(final Object o,
            @SuppressWarnings("rawtypes") final Template template,
            final boolean ifObjectArrayCanContainNullValue) throws IOException {
        AbstractTemplate.writeObject(context, o, template,
                ifObjectArrayCanContainNullValue);
        return this;
    }

    /**
     * Writes an Object out. Template is determined based on object type.
     * Pass along a template for faster results.
     */
    @Override
    public ObjectPacker writeObject(final Object o,
            final boolean ifObjectArrayCanContainNullValue) throws IOException {
        return writeObject(o, null, ifObjectArrayCanContainNullValue);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.OwriteObject(acker#writeObject(java.lang.Class)
     */
    @Override
    public ObjectPacker writeObject(final Class<?> o) throws IOException {
        return writeObject(o, basicTemplates.CLASS);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Boolean)
     */
    @Override
    public ObjectPacker writeObject(final Boolean o) throws IOException {
        return writeObject(o, basicTemplates.BOOLEAN);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Byte)
     */
    @Override
    public ObjectPacker writeObject(final Byte o) throws IOException {
        return writeObject(o, basicTemplates.BYTE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Short)
     */
    @Override
    public ObjectPacker writeObject(final Short o) throws IOException {
        return writeObject(o, basicTemplates.SHORT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Character)
     */
    @Override
    public ObjectPacker writeObject(final Character o) throws IOException {
        return writeObject(o, basicTemplates.CHARACTER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Integer)
     */
    @Override
    public ObjectPacker writeObject(final Integer o) throws IOException {
        return writeObject(o, basicTemplates.INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Long)
     */
    @Override
    public ObjectPacker writeObject(final Long o) throws IOException {
        return writeObject(o, basicTemplates.LONG);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Float)
     */
    @Override
    public ObjectPacker writeObject(final Float o) throws IOException {
        return writeObject(o, basicTemplates.FLOAT);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Double)
     */
    @Override
    public ObjectPacker writeObject(final Double o) throws IOException {
        return writeObject(o, basicTemplates.DOUBLE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.math.BigInteger)
     */
    @Override
    public ObjectPacker writeObject(final BigInteger o) throws IOException {
        return writeObject(o, basicTemplates.BIG_INTEGER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.math.BigDecimal)
     */
    @Override
    public ObjectPacker writeObject(final BigDecimal o) throws IOException {
        return writeObject(o, basicTemplates.BIG_DECIMAL);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.String)
     */
    @Override
    public ObjectPacker writeObject(final String o) throws IOException {
        return writeObject(o, basicTemplates.STRING);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(boolean[])
     */
    @Override
    public ObjectPacker writeObject(final boolean[] o) throws IOException {
        return writeObject(o, basicTemplates.BOOLEAN_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(byte[])
     */
    @Override
    public ObjectPacker writeObject(final byte[] o) throws IOException {
        return writeObject(o, basicTemplates.BYTE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(short[])
     */
    @Override
    public ObjectPacker writeObject(final short[] o) throws IOException {
        return writeObject(o, basicTemplates.SHORT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(char[])
     */
    @Override
    public ObjectPacker writeObject(final char[] o) throws IOException {
        return writeObject(o, basicTemplates.CHAR_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(int[])
     */
    @Override
    public ObjectPacker writeObject(final int[] o) throws IOException {
        return writeObject(o, basicTemplates.INT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(long[])
     */
    @Override
    public ObjectPacker writeObject(final long[] o) throws IOException {
        return writeObject(o, basicTemplates.LONG_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(float[])
     */
    @Override
    public ObjectPacker writeObject(final float[] o) throws IOException {
        return writeObject(o, basicTemplates.FLOAT_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(double[])
     */
    @Override
    public ObjectPacker writeObject(final double[] o) throws IOException {
        return writeObject(o, basicTemplates.DOUBLE_ARRAY);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.util.Date)
     */
    @Override
    public ObjectPacker writeObject(final Date o) throws IOException {
        return writeObject(o, basicTemplates.DATE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.nio.ByteBuffer)
     */
    @Override
    public ObjectPacker writeObject(final ByteBuffer o) throws IOException {
        return writeObject(o, basicTemplates.BYTE_BUFFER);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(byte[], int, int)
     */
    @Override
    public ObjectPacker writeObject(final byte[] o, final int off, final int len)
            throws IOException {
        return writeObject(new ByteArraySlice(o, off, len),
                basicTemplates.BYTE_ARRAY_SLICE);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Object)
     */
    @Override
    public ObjectPacker writeObject(final Object o) throws IOException {
        return writeObject(o, true);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.ObjectPacker#writeObject(java.lang.Object, com.blockwithme.msgpack.templates.Template)
     */
    @Override
    public ObjectPacker writeObject(final Object o, final Template<?> template)
            throws IOException {
        return writeObject(o, template, true);
    }
}
