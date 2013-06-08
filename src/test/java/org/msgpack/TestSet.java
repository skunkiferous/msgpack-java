package org.msgpack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Ignore;

@Ignore
public class TestSet {

    public void testBoolean() throws Exception {
        testBoolean(false);
        testBoolean(true);
    }

    public void testBoolean(final boolean v) throws Exception {
    }

    public void testBooleanArray() throws Exception {
        testBooleanArray(null);
        testBooleanArray(new boolean[0]);
        testBooleanArray(new boolean[] { true });
        testBooleanArray(new boolean[] { false });
        testBooleanArray(new boolean[] { true, false });
        final Random rand = new Random();
        final boolean[] v = new boolean[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextBoolean();
        }
        testBooleanArray(v);
    }

    public void testBooleanArray(final boolean[] v) throws Exception {
    }

    public void testByte() throws Exception {
        testShort((byte) 0);
        testShort((byte) -1);
        testShort((byte) 1);
        testByte(Byte.MIN_VALUE);
        testByte(Byte.MAX_VALUE);
        final byte[] bytes = new byte[1000];
        final Random rand = new Random();
        rand.nextBytes(bytes);
        for (int i = 0; i < bytes.length; ++i) {
            testByte(bytes[i]);
        }
    }

    public void testByte(final byte v) throws Exception {
    }

    public void testByteArray() throws Exception {
        testByteArray(null);
        final Random rand = new Random(System.currentTimeMillis());
        final byte[] b0 = new byte[0];
        testByteArray(b0);
        final byte[] b1 = new byte[10];
        rand.nextBytes(b1);
        testByteArray(b1);
        final byte[] b2 = new byte[1024];
        rand.nextBytes(b2);
        testByteArray(b2);
    }

    public void testByteArray(final byte[] v) throws Exception {
    }

    public void testShort() throws Exception {
        testShort((short) 0);
        testShort((short) -1);
        testShort((short) 1);
        testShort(Short.MIN_VALUE);
        testShort(Short.MAX_VALUE);
        final Random rand = new Random();
        final byte[] bytes = new byte[2000];
        rand.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i = i + 2) {
            testShort((short) ((bytes[i] << 8) | (bytes[i + 1] & 0xff)));
        }
    }

    public void testShort(final short v) throws Exception {
    }

    public void testShortArray() throws Exception {
        testShortArray(null);
        testShortArray(new short[0]);
        testShortArray(new short[] { 0 });
        testShortArray(new short[] { -1 });
        testShortArray(new short[] { 1 });
        testShortArray(new short[] { 0, -1, 1 });
        testShortArray(new short[] { Short.MIN_VALUE });
        testShortArray(new short[] { Short.MAX_VALUE });
        testShortArray(new short[] { Short.MIN_VALUE, Short.MAX_VALUE });
        final Random rand = new Random();
        final byte[] bytes = new byte[2];
        final short[] v = new short[100];
        for (int i = 0; i < v.length; ++i) {
            rand.nextBytes(bytes);
            v[i] = (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
        }
        testShortArray(v);
    }

    public void testShortArray(final short[] v) throws Exception {
    }

    public void testInteger() throws Exception {
        testInteger(0);
        testInteger(-1);
        testInteger(1);
        testInteger(Integer.MIN_VALUE);
        testInteger(Integer.MAX_VALUE);
        final Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testInteger(rand.nextInt());
        }
    }

    public void testInteger(final int v) throws Exception {
    }

    public void testIntegerArray() throws Exception {
        testIntegerArray(null);
        testIntegerArray(new int[0]);
        testIntegerArray(new int[] { 0 });
        testIntegerArray(new int[] { -1 });
        testIntegerArray(new int[] { 1 });
        testIntegerArray(new int[] { 0, -1, 1 });
        testIntegerArray(new int[] { Integer.MIN_VALUE });
        testIntegerArray(new int[] { Integer.MAX_VALUE });
        testIntegerArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE });
        final Random rand = new Random();
        final int[] v = new int[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextInt();
        }
        testIntegerArray(v);
    }

    public void testIntegerArray(final int[] v) throws Exception {
    }

    public void testLong() throws Exception {
        testLong(0);
        testLong(-1);
        testLong(1);
        testLong(Long.MIN_VALUE);
        testLong(Long.MAX_VALUE);
        final Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testLong(rand.nextLong());
        }
    }

    public void testLong(final long v) throws Exception {
    }

    public void testLongArray() throws Exception {
        testLongArray(null);
        testLongArray(new long[0]);
        testLongArray(new long[] { 0 });
        testLongArray(new long[] { -1 });
        testLongArray(new long[] { 1 });
        testLongArray(new long[] { 0, -1, 1 });
        testLongArray(new long[] { Long.MIN_VALUE });
        testLongArray(new long[] { Long.MAX_VALUE });
        testLongArray(new long[] { Long.MIN_VALUE, Long.MAX_VALUE });
        final Random rand = new Random();
        final long[] v = new long[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextLong();
        }
        testLongArray(v);
    }

    public void testLongArray(final long[] v) throws Exception {
    }

    public void testFloat() throws Exception {
        testFloat((float) 0.0);
        testFloat((float) -0.0);
        testFloat((float) 1.0);
        testFloat((float) -1.0);
        testFloat(Float.MAX_VALUE);
        testFloat(Float.MIN_VALUE);
        testFloat(Float.NaN);
        testFloat(Float.NEGATIVE_INFINITY);
        testFloat(Float.POSITIVE_INFINITY);
        final Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testFloat(rand.nextFloat());
        }
    }

    public void testFloat(final float v) throws Exception {
    }

    public void testFloatArray() throws Exception {
        testFloatArray(null);
        testFloatArray(new float[0]);
        testFloatArray(new float[] { (float) 0.0 });
        testFloatArray(new float[] { (float) -0.0 });
        testFloatArray(new float[] { (float) -1.0 });
        testFloatArray(new float[] { (float) 1.0 });
        testFloatArray(new float[] { (float) 0.0, (float) -0.0, (float) -1.0,
                (float) 1.0 });
        testFloatArray(new float[] { Float.MAX_VALUE });
        testFloatArray(new float[] { Float.MIN_VALUE });
        testFloatArray(new float[] { Float.NaN });
        testFloatArray(new float[] { Float.NEGATIVE_INFINITY });
        testFloatArray(new float[] { Float.POSITIVE_INFINITY });
        testFloatArray(new float[] { Float.MAX_VALUE, Float.MIN_VALUE,
                Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY });
        final Random rand = new Random();
        final float[] v = new float[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextFloat();
        }
        testFloatArray(v);
    }

    public void testFloatArray(final float[] v) throws Exception {
    }

    public void testDouble() throws Exception {
        testDouble(0.0);
        testDouble(-0.0);
        testDouble(1.0);
        testDouble(-1.0);
        testDouble(Double.MAX_VALUE);
        testDouble(Double.MIN_VALUE);
        testDouble(Double.NaN);
        testDouble(Double.NEGATIVE_INFINITY);
        testDouble(Double.POSITIVE_INFINITY);
        final Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testDouble(rand.nextDouble());
        }
    }

    public void testDouble(final double v) throws Exception {
    }

    public void testDoubleArray() throws Exception {
        testDoubleArray(null);
        testDoubleArray(new double[0]);
        testDoubleArray(new double[] { 0.0 });
        testDoubleArray(new double[] { -0.0 });
        testDoubleArray(new double[] { -1.0 });
        testDoubleArray(new double[] { 1.0 });
        testDoubleArray(new double[] { 0.0, -0.0, -1.0, 1.0 });
        testDoubleArray(new double[] { Double.MAX_VALUE });
        testDoubleArray(new double[] { Double.MIN_VALUE });
        testDoubleArray(new double[] { Double.NaN });
        testDoubleArray(new double[] { Double.NEGATIVE_INFINITY });
        testDoubleArray(new double[] { Double.POSITIVE_INFINITY });
        testDoubleArray(new double[] { Double.MAX_VALUE, Double.MIN_VALUE,
                Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY });
        final Random rand = new Random();
        final double[] v = new double[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextDouble();
        }
        testDoubleArray(v);
    }

    public void testDoubleArray(final double[] v) throws Exception {
    }

    public void testNil() throws Exception {
    }

    public void testString() throws Exception {
        testString(null);
        testString("");
        testString("a");
        testString("ab");
        testString("abc");
        StringBuilder sb;
        int len;
        // small size string
        {
            for (int i = 0; i < 100; i++) {
                sb = new StringBuilder();
                len = (int) Math.random() % 31 + 1;
                for (int j = 0; j < len; j++) {
                    sb.append('a' + ((int) Math.random()) & 26);
                }
                testString(sb.toString());
            }
        }
        // medium size string
        {
            for (int i = 0; i < 100; i++) {
                sb = new StringBuilder();
                len = (int) Math.random() % 100 + (1 << 15);
                for (int j = 0; j < len; j++) {
                    sb.append('a' + ((int) Math.random()) & 26);
                }
                testString(sb.toString());
            }
        }
        // large size string
        {
            for (int i = 0; i < 10; i++) {
                sb = new StringBuilder();
                len = (int) Math.random() % 100 + (1 << 31);
                for (int j = 0; j < len; j++) {
                    sb.append('a' + ((int) Math.random()) & 26);
                }
                testString(sb.toString());
            }
        }
    }

    public void testString(final String v) throws Exception {
    }

    public void testByteBuffer() throws Exception {
        testByteBuffer(null);
        final Random rand = new Random(System.currentTimeMillis());
        final byte[] b0 = new byte[0];
        testByteBuffer(ByteBuffer.wrap(b0));
        final byte[] b1 = new byte[10];
        rand.nextBytes(b1);
        testByteBuffer(ByteBuffer.wrap(b1));
        final byte[] b2 = new byte[1024];
        rand.nextBytes(b2);
        testByteBuffer(ByteBuffer.wrap(b2));
    }

    public void testByteBuffer(final ByteBuffer v) throws Exception {
    }

    public void testList() throws Exception {
        testList(null, Integer.class);
        final List<Integer> list0 = new ArrayList<Integer>();
        testList(list0, Integer.class);
        final List<Integer> list1 = new ArrayList<Integer>();
        final Random rand1 = new Random();
        for (int i = 0; i < 10; ++i) {
            list1.add(rand1.nextInt());
        }
        testList(list1, Integer.class);
        final List<String> list2 = new ArrayList<String>();
        final Random rand2 = new Random();
        for (int i = 0; i < 100; ++i) {
            list2.add("xx" + rand2.nextInt());
        }
        testList(list2, String.class);
        final List<String> list3 = new ArrayList<String>();
        final Random rand3 = new Random();
        for (int i = 0; i < 1000; ++i) {
            list3.add("xx" + rand3.nextInt());
        }
        testList(list3, String.class);
    }

    public <E> void testList(final List<E> v, final Class<E> elementClass)
            throws Exception {
    }

    public void testMap() throws Exception {
        testMap(null, Integer.class, Integer.class);
        final Map<Integer, Integer> map0 = new HashMap<Integer, Integer>();
        testMap(map0, Integer.class, Integer.class);
        final Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
        final Random rand1 = new Random();
        for (int i = 0; i < 10; ++i) {
            map1.put(rand1.nextInt(), rand1.nextInt());
        }
        testMap(map1, Integer.class, Integer.class);
        final Map<String, Integer> map2 = new HashMap<String, Integer>();
        final Random rand2 = new Random();
        for (int i = 0; i < 100; ++i) {
            map2.put("xx" + rand2.nextInt(), rand2.nextInt());
        }
        testMap(map2, String.class, Integer.class);
        final Map<String, Integer> map3 = new HashMap<String, Integer>();
        final Random rand3 = new Random();
        for (int i = 0; i < 1000; ++i) {
            map3.put("xx" + rand3.nextInt(), rand3.nextInt());
        }
        testMap(map3, String.class, Integer.class);
    }

    public <K, V> void testMap(final Map<K, V> v,
            final Class<K> keyElementClass, final Class<V> valueElementClass)
            throws Exception {
    }

    public void testBigInteger() throws Exception {
        testBigInteger(null);
        testBigInteger(BigInteger.valueOf(0));
        testBigInteger(BigInteger.valueOf(-1));
        testBigInteger(BigInteger.valueOf(1));
        testBigInteger(BigInteger.valueOf(Integer.MIN_VALUE));
        testBigInteger(BigInteger.valueOf(Integer.MAX_VALUE));
        testBigInteger(BigInteger.valueOf(Long.MIN_VALUE));
        testBigInteger(BigInteger.valueOf(Long.MAX_VALUE));
        final BigInteger max = BigInteger.valueOf(Long.MAX_VALUE).setBit(63);
        testBigInteger(max);
        final Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testBigInteger(max.subtract(BigInteger.valueOf(Math.abs(rand
                    .nextLong()))));
        }
    }

    public void testBigInteger(final BigInteger v) throws Exception {
    }

    public void testBigDecimal() throws Exception {
        testBigDecimal(null);
        testBigDecimal(BigDecimal.valueOf(0));
        testBigDecimal(BigDecimal.valueOf(-1));
        testBigDecimal(BigDecimal.valueOf(1));
        testBigDecimal(BigDecimal.valueOf(Integer.MIN_VALUE));
        testBigDecimal(BigDecimal.valueOf(Integer.MAX_VALUE));
        testBigDecimal(BigDecimal.valueOf(Long.MIN_VALUE));
        testBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    public void testBigDecimal(final BigDecimal v) throws Exception {
    }

    public void testDate() throws Exception {
        testDate(null);
        final Date d0 = new Date();
        testDate(d0);
    }

    public void testDate(final Date v) throws Exception {
    }

    public void testCharacter() throws Exception {
        testCharacter(null);
        testCharacter('a');
        testCharacter('あ');
        testCharacter((char) 1);
        testCharacter(Character.MIN_VALUE);
        testCharacter(Character.MAX_VALUE);
    }

    public void testCharacter(final Character v) throws Exception {
    }

    public void testCharArray() throws Exception {
        testCharArray(null);
        testCharArray(new char[0]);
        testCharArray(new char[] { 0 });
        testCharArray(new char[] { 1 });
        testCharArray(new char[] { 0, 1 });
        testCharArray(new char[] { Character.MIN_VALUE });
        testCharArray(new char[] { Character.MAX_VALUE });
        testCharArray(new char[] { Character.MIN_VALUE, Character.MAX_VALUE });
        final Random rand = new Random();
        final byte[] bytes = new byte[2];
        final char[] v = new char[100];
        for (int i = 0; i < v.length; ++i) {
            rand.nextBytes(bytes);
            v[i] = (char) ((bytes[0] << 8) | (bytes[1] & 0xff));
        }
        testCharArray(v);
    }

    public void testCharArray(final char[] v) throws Exception {
    }

}
