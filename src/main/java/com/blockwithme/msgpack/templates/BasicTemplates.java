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
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.blockwithme.msgpack.ObjectPacker;
import com.blockwithme.msgpack.ObjectUnpacker;
import com.blockwithme.msgpack.Packer;
import com.blockwithme.msgpack.Unpacker;

/**
 * This class contains all the basic templates, which only need to delegate to
 * the Packer/Unpacker.
 *
 * They are defined as internal classes, to simplify the package structure.
 *
 * TODO Add Collection implementation classes
 * TODO Check for other JDK Classes (EnumSet, StringBuffer, TimeZone, ...)
 * TODO Reserve Class ID up to at least 1024 for JDK and other critical API collections.
 *
 * @author monster
 */
public class BasicTemplates {

    /**
     * Own AbstractTemplate extension, which allow us to easily track all the
     * Templates defined within this class.
     */
    private static abstract class MyAbstractTemplate<T> extends
            AbstractTemplate<T> {

        /**
         * @param id
         * @param type
         */
        protected MyAbstractTemplate(final int id, final Class<T> type) {
            super(id, type);
            ALL_LIST.add(this);
        }
    }

    /** Never instantiated. */
    private BasicTemplates() {
        // NOP
    }

    /** List of all templates. */
    private static final List<Template<?>> ALL_LIST = new ArrayList<Template<?>>();

    /** All templates. */
    private static Template<?>[] ALL;

    /** The ClassNameConverter */
    private static volatile ClassNameConverter CLASS_NAME_CONVERTER = new DefaultClassNameConverter();

    /** Sets the ClassNameConverter. */
    public static void setClassNameConverter(final ClassNameConverter cnc) {
        CLASS_NAME_CONVERTER = Objects.requireNonNull(cnc);
    }

    /** The Nil/Null ID. */
    public static final int NULL_ID = 0;

    /** The (special) Object array template ID. */
    public static final int OBJECT_ARRAY_ID = 1;

    /** The (special) Enum template ID. */
    public static final int ENUM_ID = 2;

    /** The Object template ID. */
    public static final int OBJECT_ID = 3;

    /** The Class template ID. */
    public static final int CLASS_ID = 4;

    /** The Boolean Wrapper template ID. */
    public static final int BOOLEAN_ID = 5;

    /** The Byte Wrapper template ID. */
    public static final int BYTE_ID = 6;

    /** The Short Wrapper template ID. */
    public static final int SHORT_ID = 7;

    /** The Character Wrapper template ID. */
    public static final int CHARACTER_ID = 8;

    /** The Integer Wrapper template ID. */
    public static final int INTEGER_ID = 9;

    /** The Long Wrapper template ID. */
    public static final int LONG_ID = 10;

    /** The Float Wrapper template ID. */
    public static final int FLOAT_ID = 11;

    /** The Double Wrapper template ID. */
    public static final int DOUBLE_ID = 12;

    /** The BigInteger template ID. */
    public static final int BIG_INTEGER_ID = 13;

    /** The BigDecimal template ID. */
    public static final int BIG_DECIMAL_ID = 14;

    /** The String template ID. */
    public static final int STRING_ID = 15;

    /** The ByteBuffer template ID. */
    public static final int BYTE_BUFFER_ID = 16;

    /** The Date template ID. */
    public static final int DATE_ID = 17;

    /** The Boolean[][] ID. */
    public static final int BOOLEAN_ARRAY_ID = 18;

    /** The Byte array template ID. */
    public static final int BYTE_ARRAY_ID = 19;

    /** The Short array template ID. */
    public static final int SHORT_ARRAY_ID = 20;

    /** The Character array template ID. */
    public static final int CHAR_ARRAY_ID = 21;

    /** The Integer array template ID. */
    public static final int INT_ARRAY_ID = 22;

    /** The Long array template ID. */
    public static final int LONG_ARRAY_ID = 23;

    /** The Float array template ID. */
    public static final int FLOAT_ARRAY_ID = 24;

    /** The Double array template ID. */
    public static final int DOUBLE_ARRAY_ID = 25;

    /** The Boolean array array template ID. */
    public static final int BOOLEAN_ARRAY_ARRAY_ID = 26;

    /** The Byte array array template ID. */
    public static final int BYTE_ARRAY_ARRAY_ID = 27;

    /** The Short array array template ID. */
    public static final int SHORT_ARRAY_ARRAY_ID = 28;

    /** The Character array array template ID. */
    public static final int CHAR_ARRAY_ARRAY_ID = 29;

    /** The Integer array array template ID. */
    public static final int INT_ARRAY_ARRAY_ID = 30;

    /** The Long array array template ID. */
    public static final int LONG_ARRAY_ARRAY_ID = 31;

    /** The Float array array template ID. */
    public static final int FLOAT_ARRAY_ARRAY_ID = 32;

    /** The Double array array template ID. */
    public static final int DOUBLE_ARRAY_ARRAY_ID = 33;

    /**
     * The Object array template.
     *
     * The Object array template is special, because it can be use for any
     * Object[], like String[] for example.
     */
    public static final Template<Object[]> OBJECT_ARRAY = new MyAbstractTemplate<Object[]>(
            OBJECT_ARRAY_ID, Object[].class) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        protected void write2(final PackerContext context, final Object[] value)
                throws IOException {
            final ObjectPacker op = context.objectPacker;
            final Packer p = context.packer;
            final Class<?> type = value.getClass().getComponentType();
            op.write(type);
            // CANNOT USE writeArrayBegin()/writeArrayEnd() because op.write(a, typeTemp); could do multiple writes
            if (Modifier.isFinal(type.getModifiers())) {
                p.write(true);
//                p.writeArrayBegin(value.length);
                p.writeIndex(value.length);
                final Template typeTemp = context.getTemplate(type);
                for (final Object a : value) {
                    op.write(a, typeTemp);
                }
            } else {
                p.write(false);
//                p.writeArrayBegin(value.length);
                p.writeIndex(value.length);
                for (final Object a : value) {
                    op.write(a);
                }
            }
//            p.writeArrayEnd();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object[] read(final UnpackerContext context) throws IOException {
            final ObjectUnpacker ou = context.objectUnpacker;
            final Unpacker u = context.unpacker;
            final Class<?> type = ou.readClass();
            final boolean isFinal = u.readBoolean();
            final int length = u.readIndex();//u.readArrayBegin();
            System.out.println("BasicTemplates.OBJECT_ARRAY.read(): type="
                    + type + " isFinal=" + isFinal + " length=" + length);
            final Object[] array = (Object[]) Array.newInstance(type, length);
            if (isFinal) {
                @SuppressWarnings("rawtypes")
                final Template typeTemp = context.getTemplate(type);
                for (int i = 0; i < length; i++) {
                    System.out.println("BasicTemplates.OBJECT_ARRAY.read(): i="
                            + i);
                    array[i] = ou.readObject(typeTemp);
                    System.out
                            .println("BasicTemplates.OBJECT_ARRAY.read(): array["
                                    + i + "]=" + array[i]);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    System.out.println("BasicTemplates.OBJECT_ARRAY.read(): i="
                            + i);
                    array[i] = ou.readObject();
                    System.out
                            .println("BasicTemplates.OBJECT_ARRAY.read(): array["
                                    + i + "]=" + array[i]);
                }
            }
//            u.readArrayEnd(true);
            return array;
        }
    };

    /**
     * The Enum template.
     *
     * The Enum template is special, because it serves for all Enums.
     */
    @SuppressWarnings("rawtypes")
    public static final Template<Enum> ENUM = new MyAbstractTemplate<Enum>(
            ENUM_ID, Enum.class) {
        @Override
        protected void write2(final PackerContext context, final Enum value)
                throws IOException {
            context.objectPacker.write(value.getDeclaringClass());
            context.packer.writeIndex(value.ordinal());
        }

        @Override
        public Enum<?> read(final UnpackerContext context) throws IOException {
            final Class<?> declaringClass = context.objectUnpacker.readClass();
            final int ordinal = context.unpacker.readIndex();
            return (Enum<?>) declaringClass.getEnumConstants()[ordinal];
        }
    };

    /**
     * The Object template.
     *
     * WARNING: This is the (this.getClass() == java.lang.Object.class) template,
     * not the "anything and everything" template!!!
     */
    public static final Template<Object> OBJECT = new MyAbstractTemplate<Object>(
            OBJECT_ID, Object.class) {
        @Override
        protected void write2(final PackerContext context, final Object value)
                throws IOException {
            // Objects have no data, therefore, there is nothing to write!
        }

        @Override
        public Object read(final UnpackerContext context) throws IOException {
            // Objects have no data, therefore, there is nothing to read!
            return new Object();
        }
    };

    /** The Class template. */
    @SuppressWarnings("rawtypes")
    public static final Template<Class> CLASS = new MyAbstractTemplate<Class>(
            CLASS_ID, Class.class) {
        @SuppressWarnings("unchecked")
        @Override
        protected void write2(final PackerContext context, final Class value)
                throws IOException {
            final Template<?> t = context.findTemplate(value);
            if (t == null) {
                // Not a serialisable class!
                context.packer.writeIndex(0);
                // We split package and name, so that we hope for reuse of
                // package name, and therefore space saving.
                final String fqname = CLASS_NAME_CONVERTER.getName(value);
                final int index = fqname.lastIndexOf('.');
                if (index > 0) {
                    context.objectPacker.write(fqname.substring(0, index));
                    context.objectPacker.write(fqname.substring(index + 1));
                } else {
                    // Primitive type?!?
                    context.objectPacker.write("");
                    context.objectPacker.write(fqname);
                }
            } else {
                context.packer.writeIndex(t.getID());
            }
        }

        @Override
        public Class<?> read(final UnpackerContext context) throws IOException {
            final int id = context.unpacker.readIndex();
            if (id == 0) {
                final String pkg = context.objectUnpacker.readString();
                final String cls = context.objectUnpacker.readString();
                if (pkg.isEmpty()) {
                    return CLASS_NAME_CONVERTER.getClass(cls);
                }
                return CLASS_NAME_CONVERTER.getClass(pkg + '.' + cls);
            }
            return context.getTemplate(id).getType();
        }
    };

    /** The Boolean Wrapper template. */
    public static final Template<Boolean> BOOLEAN = new MyAbstractTemplate<Boolean>(
            BOOLEAN_ID, Boolean.class) {
        @Override
        protected void write2(final PackerContext context, final Boolean value)
                throws IOException {
            context.packer.write(value.booleanValue());
        }

        @Override
        public Boolean read(final UnpackerContext context) throws IOException {
            return context.unpacker.readBoolean();
        }
    };

    /** The Byte Wrapper template. */
    public static final Template<Byte> BYTE = new MyAbstractTemplate<Byte>(
            BYTE_ID, Byte.class) {
        @Override
        protected void write2(final PackerContext context, final Byte value)
                throws IOException {
            context.packer.write(value.byteValue());
        }

        @Override
        public Byte read(final UnpackerContext context) throws IOException {
            return context.unpacker.readByte();
        }
    };

    /** The Short Wrapper template. */
    public static final Template<Short> SHORT = new MyAbstractTemplate<Short>(
            SHORT_ID, Short.class) {
        @Override
        protected void write2(final PackerContext context, final Short value)
                throws IOException {
            context.packer.write(value.shortValue());
        }

        @Override
        public Short read(final UnpackerContext context) throws IOException {
            return context.unpacker.readShort();
        }
    };

    /** The Character Wrapper template. */
    public static final Template<Character> CHARACTER = new MyAbstractTemplate<Character>(
            CHARACTER_ID, Character.class) {
        @Override
        protected void write2(final PackerContext context, final Character value)
                throws IOException {
            context.packer.write(value.charValue());
        }

        @Override
        public Character read(final UnpackerContext context) throws IOException {
            return context.unpacker.readChar();
        }
    };

    /** The Integer Wrapper template. */
    public static final Template<Integer> INTEGER = new MyAbstractTemplate<Integer>(
            INTEGER_ID, Integer.class) {
        @Override
        protected void write2(final PackerContext context, final Integer value)
                throws IOException {
            context.packer.write(value.intValue());
        }

        @Override
        public Integer read(final UnpackerContext context) throws IOException {
            return context.unpacker.readInt();
        }
    };

    /** The Long Wrapper template. */
    public static final Template<Long> LONG = new MyAbstractTemplate<Long>(
            LONG_ID, Long.class) {
        @Override
        protected void write2(final PackerContext context, final Long value)
                throws IOException {
            context.packer.write(value.longValue());
        }

        @Override
        public Long read(final UnpackerContext context) throws IOException {
            return context.unpacker.readLong();
        }
    };

    /** The Float Wrapper template. */
    public static final Template<Float> FLOAT = new MyAbstractTemplate<Float>(
            FLOAT_ID, Float.class) {
        @Override
        protected void write2(final PackerContext context, final Float value)
                throws IOException {
            context.packer.write(value.floatValue());
        }

        @Override
        public Float read(final UnpackerContext context) throws IOException {
            return context.unpacker.readFloat();
        }
    };

    /** The Double Wrapper template. */
    public static final Template<Double> DOUBLE = new MyAbstractTemplate<Double>(
            DOUBLE_ID, Double.class) {
        @Override
        protected void write2(final PackerContext context, final Double value)
                throws IOException {
            context.packer.write(value.doubleValue());
        }

        @Override
        public Double read(final UnpackerContext context) throws IOException {
            return context.unpacker.readDouble();
        }
    };

    /** The BigInteger template. */
    public static final Template<BigInteger> BIG_INTEGER = new MyAbstractTemplate<BigInteger>(
            BIG_INTEGER_ID, BigInteger.class) {
        @Override
        protected void write2(final PackerContext context,
                final BigInteger value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public BigInteger read(final UnpackerContext context)
                throws IOException {
            return context.unpacker.readBigInteger();
        }
    };

    /** The BigDecimal template. */
    public static final Template<BigDecimal> BIG_DECIMAL = new MyAbstractTemplate<BigDecimal>(
            BIG_DECIMAL_ID, BigDecimal.class) {
        @Override
        protected void write2(final PackerContext context,
                final BigDecimal value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public BigDecimal read(final UnpackerContext context)
                throws IOException {
            return context.unpacker.readBigDecimal();
        }
    };

    /** The String template. */
    public static final Template<String> STRING = new MyAbstractTemplate<String>(
            STRING_ID, String.class) {
        @Override
        protected void write2(final PackerContext context, final String value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public String read(final UnpackerContext context) throws IOException {
            return context.unpacker.readString();
        }
    };

    /** The ByteBuffer template. */
    public static final Template<ByteBuffer> BYTE_BUFFER = new MyAbstractTemplate<ByteBuffer>(
            BYTE_BUFFER_ID, ByteBuffer.class) {
        @Override
        protected void write2(final PackerContext context,
                final ByteBuffer value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public ByteBuffer read(final UnpackerContext context)
                throws IOException {
            return context.unpacker.readByteBuffer();
        }
    };

    /** The Date template. */
    public static final Template<Date> DATE = new MyAbstractTemplate<Date>(
            DATE_ID, Date.class) {
        @Override
        protected void write2(final PackerContext context, final Date value)
                throws IOException {
            context.packer.write(value.getTime());
        }

        @Override
        public Date read(final UnpackerContext context) throws IOException {
            return new Date(context.unpacker.readLong());
        }
    };

    /** The Boolean array template. */
    public static final Template<boolean[]> BOOLEAN_ARRAY = new MyAbstractTemplate<boolean[]>(
            BOOLEAN_ARRAY_ID, boolean[].class) {
        @Override
        protected void write2(final PackerContext context, final boolean[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public boolean[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readBooleanArray();
        }
    };

    /** The Byte array template. */
    public static final Template<byte[]> BYTE_ARRAY = new MyAbstractTemplate<byte[]>(
            BYTE_ARRAY_ID, byte[].class) {
        @Override
        protected void write2(final PackerContext context, final byte[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public byte[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readByteArray();
        }
    };

    /** The Short array template. */
    public static final Template<short[]> SHORT_ARRAY = new MyAbstractTemplate<short[]>(
            SHORT_ARRAY_ID, short[].class) {
        @Override
        protected void write2(final PackerContext context, final short[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public short[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readShortArray();
        }
    };

    /** The Character array template. */
    public static final Template<char[]> CHAR_ARRAY = new MyAbstractTemplate<char[]>(
            CHAR_ARRAY_ID, char[].class) {
        @Override
        protected void write2(final PackerContext context, final char[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public char[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readCharArray();
        }
    };

    /** The Integer array template. */
    public static final Template<int[]> INT_ARRAY = new MyAbstractTemplate<int[]>(
            INT_ARRAY_ID, int[].class) {
        @Override
        protected void write2(final PackerContext context, final int[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public int[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readIntArray();
        }
    };

    /** The Long array template. */
    public static final Template<long[]> LONG_ARRAY = new MyAbstractTemplate<long[]>(
            LONG_ARRAY_ID, long[].class) {
        @Override
        protected void write2(final PackerContext context, final long[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public long[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readLongArray();
        }
    };

    /** The Float array template. */
    public static final Template<float[]> FLOAT_ARRAY = new MyAbstractTemplate<float[]>(
            FLOAT_ARRAY_ID, float[].class) {
        @Override
        protected void write2(final PackerContext context, final float[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public float[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readFloatArray();
        }
    };

    /** The Double array template. */
    public static final Template<double[]> DOUBLE_ARRAY = new MyAbstractTemplate<double[]>(
            DOUBLE_ARRAY_ID, double[].class) {
        @Override
        protected void write2(final PackerContext context, final double[] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public double[] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readDoubleArray();
        }
    };

    /** The Boolean array array template. */
    public static final Template<boolean[][]> BOOLEAN_ARRAY_ARRAY = new MyAbstractTemplate<boolean[][]>(
            BOOLEAN_ARRAY_ARRAY_ID, boolean[][].class) {
        @Override
        protected void write2(final PackerContext context,
                final boolean[][] value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public boolean[][] read(final UnpackerContext context)
                throws IOException {
            return context.unpacker.readBooleanArrayArray();
        }
    };

    /** The Byte array array template. */
    public static final Template<byte[][]> BYTE_ARRAY_ARRAY = new MyAbstractTemplate<byte[][]>(
            BYTE_ARRAY_ARRAY_ID, byte[][].class) {
        @Override
        protected void write2(final PackerContext context, final byte[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public byte[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readByteArrayArray();
        }
    };

    /** The Short array array template. */
    public static final Template<short[][]> SHORT_ARRAY_ARRAY = new MyAbstractTemplate<short[][]>(
            SHORT_ARRAY_ARRAY_ID, short[][].class) {
        @Override
        protected void write2(final PackerContext context, final short[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public short[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readShortArrayArray();
        }
    };

    /** The Character array array template. */
    public static final Template<char[][]> CHAR_ARRAY_ARRAY = new MyAbstractTemplate<char[][]>(
            CHAR_ARRAY_ARRAY_ID, char[][].class) {
        @Override
        protected void write2(final PackerContext context, final char[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public char[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readCharArrayArray();
        }
    };

    /** The Integer array array template. */
    public static final Template<int[][]> INT_ARRAY_ARRAY = new MyAbstractTemplate<int[][]>(
            INT_ARRAY_ARRAY_ID, int[][].class) {
        @Override
        protected void write2(final PackerContext context, final int[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public int[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readIntArrayArray();
        }
    };

    /** The Long array array template. */
    public static final Template<long[][]> LONG_ARRAY_ARRAY = new MyAbstractTemplate<long[][]>(
            LONG_ARRAY_ARRAY_ID, long[][].class) {
        @Override
        protected void write2(final PackerContext context, final long[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public long[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readLongArrayArray();
        }
    };

    /** The Float array array template. */
    public static final Template<float[][]> FLOAT_ARRAY_ARRAY = new MyAbstractTemplate<float[][]>(
            FLOAT_ARRAY_ARRAY_ID, float[][].class) {
        @Override
        protected void write2(final PackerContext context, final float[][] value)
                throws IOException {
            context.packer.write(value);
        }

        @Override
        public float[][] read(final UnpackerContext context) throws IOException {
            return context.unpacker.readFloatArrayArray();
        }
    };

    /** The Double array array template. */
    public static final Template<double[][]> DOUBLE_ARRAY_ARRAY = new MyAbstractTemplate<double[][]>(
            DOUBLE_ARRAY_ARRAY_ID, double[][].class) {
        @Override
        protected void write2(final PackerContext context,
                final double[][] value) throws IOException {
            context.packer.write(value);
        }

        @Override
        public double[][] read(final UnpackerContext context)
                throws IOException {
            return context.unpacker.readDoubleArrayArray();
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
