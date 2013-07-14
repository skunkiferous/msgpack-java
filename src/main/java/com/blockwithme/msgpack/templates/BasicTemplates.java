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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.blockwithme.msgpack.ObjectPacker;
import com.blockwithme.msgpack.ObjectUnpacker;
import com.blockwithme.msgpack.Packer;
import com.blockwithme.msgpack.Unpacker;
import com.blockwithme.msgpack.impl.ByteArraySlice;

/**
 * This class contains all the basic templates, which only need to delegate to
 * the Packer/Unpacker.
 *
 * They are defined as internal classes, to simplify the package structure.
 *
 * TODO Add (more) Collection implementation classes
 * TODO Check for other JDK Classes (EnumSet, StringBuffer, TimeZone, ...)
 * TODO Reserve Class ID up to at least 1024 for JDK and other critical API collections.
 *
 * @author monster
 */
public class BasicTemplates {

    /**
     * Own AbstractTemplate extension, which allow us to easily track all the
     * Templates defined within BasicTemplates.
     */
    private static abstract class MyAbstractTemplate<T> extends
            AbstractTemplate<T> {

        /**
         * @param id
         * @param type
         * @param isListType
         * @param isMergeable
         */
        protected MyAbstractTemplate(final int id, final Class<T> type,
                final boolean isListType, final boolean isMergeable) {
            this(id, type, isListType, isMergeable, -1);
        }

        /**
         * @param id
         * @param type
         * @param isListType
         * @param isMergeable
         * @param fixedSize
         */
        protected MyAbstractTemplate(final int id, final Class<T> type,
                final boolean isListType, final boolean isMergeable,
                final int fixedSize) {
            super(id, type, isListType, isMergeable, fixedSize);
            while (ALL_LIST.size() <= id) {
                ALL_LIST.add(null);
            }
            ALL_LIST.set(id, this);
        }
    }

    /**
     * AbstractTemplate for primitive arrays.
     *
     * Primitive array templates do NOT pre-create, because by definition,
     * primitive arrays cannot cause cycles.
     */
    private static abstract class MyPrimitiveArrayAbstractTemplate<T> extends
            MyAbstractTemplate<T> {

        /**
         * @param id
         * @param type
         */
        protected MyPrimitiveArrayAbstractTemplate(final int id,
                final Class<T> type) {
            super(id, type, true, false);
        }
    }

    /**
     * The Abstract Collection template.
     *
     * It is a complete Collection Template implementation, except for
     * preCreate(int)
     *
     * The collection is stored as an "array" object.
     */
    @SuppressWarnings("rawtypes")
    private static abstract class AbstractCollectionTemplate<C extends Collection<?>>
            extends MyAbstractTemplate<C> {

        /**
         * @param id
         * @param type
         */
        protected AbstractCollectionTemplate(final int id, final Class<C> type) {
            super(id, type, true, false);
        }

        /** Writes the collection. */
        @Override
        public final void writeData(final PackerContext context,
                final int size, final C value) throws IOException {
            final ObjectPacker p = context.objectPacker;
            for (final Object o : value) {
                p.writeObject(o);
            }
        }

        /** Reads the collection. */
        @SuppressWarnings("unchecked")
        @Override
        public final C readData(final UnpackerContext context,
                final C preCreated, final int size) throws IOException {
            final ObjectUnpacker ou = context.objectUnpacker;
            final Collection c = preCreated;
            for (int i = 0; i < size; i++) {
                c.add(ou.readObject());
            }
            return preCreated;
        }

        /** Computes the size. */
        @Override
        public final int getSpaceRequired(final PackerContext context, final C v) {
            return v.size();
        }

        /** The only thing you need to implement. */
        @Override
        public abstract C preCreate(final int size);
    };

    /**
     * The Abstract Map template.
     *
     * It is a complete Map Template implementation, except for
     * preCreate(int)
     *
     * The Map is stored as a "map" object.
     */
    @SuppressWarnings("rawtypes")
    private static abstract class AbstractMapTemplate<M extends Map<?, ?>>
            extends MyAbstractTemplate<M> {

        /**
         * @param id
         * @param type
         */
        protected AbstractMapTemplate(final int id, final Class<M> type) {
            super(id, type, false, false);
        }

        /** Writes the Map */
        @Override
        public final void writeData(final PackerContext context,
                final int size, final M value) throws IOException {
            final ObjectPacker p = context.objectPacker;
            for (final Map.Entry o : value.entrySet()) {
                p.writeObject(o.getKey());
                p.writeObject(o.getValue());
            }
        }

        /** Reads the Map */
        @SuppressWarnings("unchecked")
        @Override
        public final M readData(final UnpackerContext context,
                final M preCreated, final int size) throws IOException {
            readHeaderValue(context, preCreated, size);
            final ObjectUnpacker ou = context.objectUnpacker;
            final Map c = preCreated;
            for (int i = 0; i < size; i++) {
                final Object key = ou.readObject();
                final Object value = ou.readObject();
                c.put(key, value);
            }
            return preCreated;
        }

        /** Computes the size; (key+value) counts as *1*. */
        @Override
        public final int getSpaceRequired(final PackerContext context, final M v) {
            return v.size();
        }

        /**
         * Skips the "unused header value" by default.
         * But you could use it for something ...
         *
         * @see AbstractTemplate.writeMapHeaderValue(PackerContext, T, int)
         */
        protected void readHeaderValue(final UnpackerContext context,
                final M preCreated, final int size) throws IOException {
            context.unpacker.skip();
        }

        /** The only thing you need to implement. */
        @Override
        public abstract M preCreate(final int size);
    };

    /** Never instantiated. */
    private BasicTemplates() {
        // NOP
    }

    /** List of all basic templates. */
    private static final List<Template<?>> ALL_LIST = new ArrayList<Template<?>>();

    /** All templates. */
    private static Template<?>[] ALL;

    /** The Object template ID. */
    public static final int OBJECT_ID = 0;

    /** The Boolean Wrapper template ID. */
    public static final int BOOLEAN_ID = 1;

    /** The Byte Wrapper template ID. */
    public static final int BYTE_ID = 2;

    /** The Short Wrapper template ID. */
    public static final int SHORT_ID = 3;

    /** The Character Wrapper template ID. */
    public static final int CHARACTER_ID = 4;

    /** The Integer Wrapper template ID. */
    public static final int INTEGER_ID = 5;

    /** The Long Wrapper template ID. */
    public static final int LONG_ID = 6;

    /** The Float Wrapper template ID. */
    public static final int FLOAT_ID = 7;

    /** The Double Wrapper template ID. */
    public static final int DOUBLE_ID = 8;

    /** The BigInteger template ID. */
    public static final int BIG_INTEGER_ID = 9;

    /** The BigDecimal template ID. */
    public static final int BIG_DECIMAL_ID = 10;

    /** The String template ID. */
    public static final int STRING_ID = 11;

    /** The Class template ID. */
    public static final int CLASS_ID = 12;

    /** The Date template ID. */
    public static final int DATE_ID = 13;

    /** The Boolean[][] ID. */
    public static final int BOOLEAN_ARRAY_ID = 14;

    /** The Byte array template ID. */
    public static final int BYTE_ARRAY_ID = 15;

    /** The Short array template ID. */
    public static final int SHORT_ARRAY_ID = 16;

    /** The Character array template ID. */
    public static final int CHAR_ARRAY_ID = 17;

    /** The Integer array template ID. */
    public static final int INT_ARRAY_ID = 18;

    /** The Long array template ID. */
    public static final int LONG_ARRAY_ID = 19;

    /** The Float array template ID. */
    public static final int FLOAT_ARRAY_ID = 20;

    /** The Double array template ID. */
    public static final int DOUBLE_ARRAY_ID = 21;

    /** The ByteBuffer template ID. */
    public static final int BYTE_BUFFER_ID = 22;

    // Collection classes

    /** The java.util.ArrayList template ID. */
    public static final int JAVA_UTIL_ARRAY_LIST_ID = 23;

    /** The java.util.HashSet template ID. */
    public static final int JAVA_UTIL_HASH_SET_ID = 24;

    /** The java.util.HashMap template ID. */
    public static final int JAVA_UTIL_HASH_MAP_ID = 25;

    /**
     * The Object template.
     *
     * WARNING: This is the (this.getClass() == java.lang.Object.class) template,
     * not the "anything and everything" template!!!
     */
    public static final Template<Object> OBJECT = new MyAbstractTemplate<Object>(
            OBJECT_ID, Object.class, true, false) {
        @Override
        public int getSpaceRequired(final PackerContext context, final Object v) {
            return 0;
        }

        @Override
        public void writeData(final PackerContext context, final int size,
                final Object v) throws IOException {
            // Objects have no data, therefore, there is nothing to write!
        }

        @Override
        public Object readData(final UnpackerContext context,
                final Object preCreated, final int size) throws IOException {
            // Objects have no data, therefore, there is nothing to read!
            return new Object();
        }
    };

    /** The Class template. */
    @SuppressWarnings("rawtypes")
    public static final Template<Class> CLASS = new MyAbstractTemplate<Class>(
            CLASS_ID, Class.class, true, true) {

        @SuppressWarnings("unchecked")
        @Override
        public int getSpaceRequired(final PackerContext context, final Class v) {
            // Space depends on if this is a class that we use while serializing.
            return (context.findTemplate(v) == null) ? 3 : 1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void writeData(final PackerContext context, final int size,
                final Class value) throws IOException {
            final Template<?> t = context.findTemplate(value);
            if (t == null) {
                // Not a serialisable class!
                context.packer.writeNil();
                // We split package and name, so that we hope for reuse of
                // package name, and therefore space saving.
                final String fqname = AbstractTemplate.getClassNameConverter()
                        .getName(value);
                final int index = fqname.lastIndexOf('.');
                if (index > 0) {
                    context.objectPacker
                            .writeObject(fqname.substring(0, index));
                    context.objectPacker.writeObject(fqname
                            .substring(index + 1));
                } else {
                    // Primitive type?!?
                    context.objectPacker.writeObject("");
                    context.objectPacker.writeObject(fqname);
                }
            } else {
                // A serializable class
                final int depth = AbstractTemplate.getArrayDepth(value);
                context.packer.writeIndex(t.getID() * 4 + depth);
            }
        }

        @Override
        public Class<?> readData(final UnpackerContext context,
                final Class preCreated, final int size) throws IOException {
            if (context.unpacker.trySkipNil()) {
                // OK, a non-serializable Class
                final String pkg = context.objectUnpacker.readString();
                final String cls = context.objectUnpacker.readString();
                if (pkg.isEmpty()) {
                    return AbstractTemplate.getClassNameConverter().getClass(
                            cls);
                }
                return AbstractTemplate.getClassNameConverter().getClass(
                        pkg + '.' + cls);
            }
            // A serialisable class
            final int id = context.unpacker.readIndex();
            return context.getTemplate(id / 4).getType();
        }
    };

    /** The Boolean Wrapper template. */
    public static final Template<Boolean> BOOLEAN = new MyAbstractTemplate<Boolean>(
            BOOLEAN_ID, Boolean.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Boolean value) throws IOException {
            context.packer.writeBoolean(value.booleanValue());
        }

        @Override
        public Boolean readData(final UnpackerContext context,
                final Boolean preCreated, final int size) throws IOException {
            return context.unpacker.readBoolean();
        }
    };

    /** The Byte Wrapper template. */
    public static final Template<Byte> BYTE = new MyAbstractTemplate<Byte>(
            BYTE_ID, Byte.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Byte value) throws IOException {
            context.packer.writeByte(value.byteValue());
        }

        @Override
        public Byte readData(final UnpackerContext context,
                final Byte preCreated, final int size) throws IOException {
            return context.unpacker.readByte();
        }
    };

    /** The Short Wrapper template. */
    public static final Template<Short> SHORT = new MyAbstractTemplate<Short>(
            SHORT_ID, Short.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Short value) throws IOException {
            context.packer.writeShort(value.shortValue());
        }

        @Override
        public Short readData(final UnpackerContext context,
                final Short preCreated, final int size) throws IOException {
            return context.unpacker.readShort();
        }
    };

    /** The Character Wrapper template. */
    public static final Template<Character> CHARACTER = new MyAbstractTemplate<Character>(
            CHARACTER_ID, Character.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Character value) throws IOException {
            context.packer.writeChar(value.charValue());
        }

        @Override
        public Character readData(final UnpackerContext context,
                final Character preCreated, final int size) throws IOException {
            return context.unpacker.readChar();
        }
    };

    /** The Integer Wrapper template. */
    public static final Template<Integer> INTEGER = new MyAbstractTemplate<Integer>(
            INTEGER_ID, Integer.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Integer value) throws IOException {
            context.packer.writeInt(value.intValue());
        }

        @Override
        public Integer readData(final UnpackerContext context,
                final Integer preCreated, final int size) throws IOException {
            return context.unpacker.readInt();
        }
    };

    /** The Long Wrapper template. */
    public static final Template<Long> LONG = new MyAbstractTemplate<Long>(
            LONG_ID, Long.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Long value) throws IOException {
            context.packer.writeLong(value.longValue());
        }

        @Override
        public Long readData(final UnpackerContext context,
                final Long preCreated, final int size) throws IOException {
            return context.unpacker.readLong();
        }
    };

    /** The Float Wrapper template. */
    public static final Template<Float> FLOAT = new MyAbstractTemplate<Float>(
            FLOAT_ID, Float.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Float value) throws IOException {
            context.packer.writeFloat(value.floatValue());
        }

        @Override
        public Float readData(final UnpackerContext context,
                final Float preCreated, final int size) throws IOException {
            return context.unpacker.readFloat();
        }
    };

    /** The Double Wrapper template. */
    public static final Template<Double> DOUBLE = new MyAbstractTemplate<Double>(
            DOUBLE_ID, Double.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Double value) throws IOException {
            context.packer.writeDouble(value.doubleValue());
        }

        @Override
        public Double readData(final UnpackerContext context,
                final Double preCreated, final int size) throws IOException {
            return context.unpacker.readDouble();
        }
    };

    /** The BigInteger template. */
    public static final Template<BigInteger> BIG_INTEGER = new MyAbstractTemplate<BigInteger>(
            BIG_INTEGER_ID, BigInteger.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final BigInteger value) throws IOException {
            context.packer.writeBigInteger(value);
        }

        @Override
        public BigInteger readData(final UnpackerContext context,
                final BigInteger preCreated, final int size) throws IOException {
            return context.unpacker.readBigInteger();
        }
    };

    /** The BigDecimal template. */
    public static final Template<BigDecimal> BIG_DECIMAL = new MyAbstractTemplate<BigDecimal>(
            BIG_DECIMAL_ID, BigDecimal.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final BigDecimal value) throws IOException {
            context.packer.writeBigDecimal(value);
        }

        @Override
        public BigDecimal readData(final UnpackerContext context,
                final BigDecimal preCreated, final int size) throws IOException {
            return context.unpacker.readBigDecimal();
        }
    };

    /** The String template. */
    public static final Template<String> STRING = new MyAbstractTemplate<String>(
            STRING_ID, String.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final String value) throws IOException {
            context.packer.writeUTF(value);
        }

        @Override
        public String readData(final UnpackerContext context,
                final String preCreated, final int size) throws IOException {
            return context.unpacker.readUTF();
        }
    };

    /** The ByteBuffer template. */
    public static final Template<ByteBuffer> BYTE_BUFFER = new MyAbstractTemplate<ByteBuffer>(
            BYTE_BUFFER_ID, ByteBuffer.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final ByteBuffer value) throws IOException {
            context.packer.writeByteBuffer(value);
        }

        @Override
        public ByteBuffer readData(final UnpackerContext context,
                final ByteBuffer preCreated, final int size) throws IOException {
            return context.unpacker.readByteBuffer();
        }
    };

    /** The Date template. */
    public static final Template<Date> DATE = new MyAbstractTemplate<Date>(
            DATE_ID, Date.class, true, true, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final Date value) throws IOException {
            context.packer.writeDate(value);
        }

        @Override
        public Date readData(final UnpackerContext context,
                final Date preCreated, final int size) throws IOException {
            return new Date(context.unpacker.readLong());
        }
    };

    /** The Boolean array template. */
    public static final Template<boolean[]> BOOLEAN_ARRAY = new MyPrimitiveArrayAbstractTemplate<boolean[]>(
            BOOLEAN_ARRAY_ID, boolean[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final boolean[] value) throws IOException {
            final Packer p = context.packer;
            for (final boolean a : value) {
                p.writeBoolean(a);
            }
        }

        @Override
        public boolean[] readData(final UnpackerContext context,
                final boolean[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final boolean[] result = new boolean[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readBoolean();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context,
                final boolean[] v) {
            return v.length;
        }
    };

    /** The Byte array template. */
    public static final Template<byte[]> BYTE_ARRAY = new MyAbstractTemplate<byte[]>(
            BYTE_ARRAY_ID, byte[].class, true, false, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final byte[] value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public byte[] readData(final UnpackerContext context,
                final byte[] preCreated, final int size) throws IOException {
            return context.unpacker.readByteArray();
        }
    };

    /** The Short array template. */
    public static final Template<short[]> SHORT_ARRAY = new MyPrimitiveArrayAbstractTemplate<short[]>(
            SHORT_ARRAY_ID, short[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final short[] value) throws IOException {
            final Packer p = context.packer;
            for (final short a : value) {
                p.writeShort(a);
            }
        }

        @Override
        public short[] readData(final UnpackerContext context,
                final short[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final short[] result = new short[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readShort();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context, final short[] v) {
            return v.length;
        }
    };

    /** The Character array template. */
    public static final Template<char[]> CHAR_ARRAY = new MyPrimitiveArrayAbstractTemplate<char[]>(
            CHAR_ARRAY_ID, char[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final char[] value) throws IOException {
            final Packer p = context.packer;
            for (final char a : value) {
                p.writeChar(a);
            }
        }

        @Override
        public char[] readData(final UnpackerContext context,
                final char[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final char[] result = new char[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readChar();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context, final char[] v) {
            return v.length;
        }
    };

    /** The Integer array template. */
    public static final Template<int[]> INT_ARRAY = new MyPrimitiveArrayAbstractTemplate<int[]>(
            INT_ARRAY_ID, int[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final int[] value) throws IOException {
            final Packer p = context.packer;
            for (final int a : value) {
                p.writeInt(a);
            }
        }

        @Override
        public int[] readData(final UnpackerContext context,
                final int[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final int[] result = new int[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readInt();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context, final int[] v) {
            return v.length;
        }
    };

    /** The Long array template. */
    public static final Template<long[]> LONG_ARRAY = new MyPrimitiveArrayAbstractTemplate<long[]>(
            LONG_ARRAY_ID, long[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final long[] value) throws IOException {
            final Packer p = context.packer;
            for (final long a : value) {
                p.writeLong(a);
            }
        }

        @Override
        public long[] readData(final UnpackerContext context,
                final long[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final long[] result = new long[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readLong();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context, final long[] v) {
            return v.length;
        }
    };

    /** The Float array template. */
    public static final Template<float[]> FLOAT_ARRAY = new MyPrimitiveArrayAbstractTemplate<float[]>(
            FLOAT_ARRAY_ID, float[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final float[] value) throws IOException {
            final Packer p = context.packer;
            for (final float a : value) {
                p.writeFloat(a);
            }
        }

        @Override
        public float[] readData(final UnpackerContext context,
                final float[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final float[] result = new float[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readFloat();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context, final float[] v) {
            return v.length;
        }
    };

    /** The Double array template. */
    public static final Template<double[]> DOUBLE_ARRAY = new MyPrimitiveArrayAbstractTemplate<double[]>(
            DOUBLE_ARRAY_ID, double[].class) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final double[] value) throws IOException {
            final Packer p = context.packer;
            for (final double a : value) {
                p.writeDouble(a);
            }
        }

        @Override
        public double[] readData(final UnpackerContext context,
                final double[] preCreated, final int size) throws IOException {
            final Unpacker u = context.unpacker;
            final double[] result = new double[size];
            for (int i = 0; i < size; i++) {
                result[i] = u.readDouble();
            }
            return result;
        }

        @Override
        public int getSpaceRequired(final PackerContext context,
                final double[] v) {
            return v.length;
        }
    };

    /** The Byte array slice template. */
    public static final Template<ByteArraySlice> BYTE_ARRAY_SLICE = new MyAbstractTemplate<ByteArraySlice>(
            BYTE_ARRAY_ID, ByteArraySlice.class, true, false, 1) {
        @Override
        public void writeData(final PackerContext context, final int size,
                final ByteArraySlice value) throws IOException {
            context.packer.write(value.o, value.off, value.len);
        }

        @Override
        public ByteArraySlice readData(final UnpackerContext context,
                final ByteArraySlice preCreated, final int size)
                throws IOException {
            final byte[] bytes = context.unpacker.readByteArray();
            return (bytes == null) ? null : new ByteArraySlice(bytes, 0,
                    bytes.length);
        }
    };

    /** The ArrayList template. */
    @SuppressWarnings("rawtypes")
    public static final Template<ArrayList> JAVA_UTIL_ARRAY_LIST = new AbstractCollectionTemplate<ArrayList>(
            JAVA_UTIL_ARRAY_LIST_ID, ArrayList.class) {

        @Override
        public ArrayList preCreate(final int size) {
            return new ArrayList(size);
        }
    };

    /** The HashSet template. */
    @SuppressWarnings("rawtypes")
    public static final Template<HashSet> JAVA_UTIL_HASH_SET = new AbstractCollectionTemplate<HashSet>(
            JAVA_UTIL_HASH_SET_ID, HashSet.class) {

        @Override
        public HashSet preCreate(final int size) {
            return new HashSet(size);
        }
    };

    /** The HashMap template. */
    @SuppressWarnings("rawtypes")
    public static final Template<HashMap> JAVA_UTIL_HASH_MAP = new AbstractMapTemplate<HashMap>(
            JAVA_UTIL_HASH_MAP_ID, HashMap.class) {

        @Override
        public HashMap preCreate(final int size) {
            return new HashMap(size);
        }
    };

    /** Returns all basic templates. */
    public static synchronized Template<?>[] getAllBasicTemplates() {
        if (ALL == null) {
            ALL = ALL_LIST.toArray(new Template[ALL_LIST.size()]);
        }
        return ALL;
    }
}
