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
package com.blockwithme.msgpack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;

import com.blockwithme.msgpack.impl.MessagePackPacker;
import com.blockwithme.msgpack.impl.MessagePackUnpacker;
import com.blockwithme.msgpack.impl.ObjectPackerImpl;
import com.blockwithme.msgpack.impl.ObjectUnpackerImpl;
import com.blockwithme.msgpack.templates.AbstractTemplate;
import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.ObjectType;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates.UnpackerContext;
import com.blockwithme.util.DataInputBuffer;
import com.blockwithme.util.DataOutputBuffer;

/**
 * @author monster
 *
 */
public class TestSimple {
    private static class TestRaw {
        public int value;
    }

    private static final class TestFixed {
        public boolean v1;
        public boolean v2;
    }

    @SuppressWarnings("rawtypes")
    private static final Template[] basic = BasicTemplates
            .getAllBasicTemplates();

    @SuppressWarnings("rawtypes")
    private static final Template[] extended = Arrays.copyOf(basic,
            basic.length + 2);

    static {
        extended[basic.length] = new AbstractTemplate<TestRaw>(basic.length,
                TestRaw.class, ObjectType.RAW, false, 1) {

            @Override
            public void writeData(final PackerContext context, final int size,
                    final TestRaw v) throws IOException {
                final Packer p = context.packer;
                p.writeRawBegin(4);
                p.dataOutput().writeInt(v.value);
                p.rawWritten(4);
                p.writeRawEnd();
            }

            @Override
            public TestRaw readData(final UnpackerContext context,
                    final TestRaw preCreated, final int size)
                    throws IOException {
                final Unpacker u = context.unpacker;
                final int raw = u.readRawBegin();
                if (raw != 4) {
                    throw new IOException("raw: " + raw);
                }
                final TestRaw result = new TestRaw();
                result.value = u.dataInput().readInt();
                u.rawRead(4);
                u.readRawEnd();
                return result;
            }
        };
        extended[basic.length + 1] = new AbstractTemplate<TestFixed>(
                basic.length + 1, TestFixed.class, ObjectType.ARRAY, false, 2) {

            @Override
            public void writeData(final PackerContext context, final int size,
                    final TestFixed v) throws IOException {
                final Packer p = context.packer;
                p.writeBoolean(v.v1);
                p.writeBoolean(v.v2);
            }

            @Override
            public TestFixed readData(final UnpackerContext context,
                    final TestFixed preCreated, final int size)
                    throws IOException {
                final Unpacker u = context.unpacker;
                final TestFixed result = new TestFixed();
                result.v1 = u.readBoolean();
                result.v2 = u.readBoolean();
                return result;
            }
        };
    }

    private DataOutputBuffer newDataOutputBuffer() {
        return new DataOutputBuffer(2048);
    }

    private MessagePackPacker newPacker(final DataOutputBuffer dob) {
        return new MessagePackPacker(dob);
    }

    private ObjectPackerImpl newObjectPacker(final DataOutputBuffer dob) {
        return new ObjectPackerImpl(newPacker(dob), new PackerContext(extended));
    }

    private void dumpOP(final DataOutputBuffer dob) {
        final byte[] bytes = dob.buffer();
        final int size = dob.size();
        final StringBuilder buf0 = new StringBuilder(size * 2);
        final StringBuilder buf1 = new StringBuilder(size * 2);
        final StringBuilder buf2 = new StringBuilder(size * 2);
        for (int i = 0; i < size; i++) {
            if (i < 10) {
                buf0.append('0');
            }
            buf0.append(i);
            String s = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
            if (s.length() == 1) {
                s = "0" + s;
            }
            buf1.append(s);
            final char c = (char) bytes[i];
            buf2.append(' ');
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                buf2.append(c);
            } else {
                buf2.append('?');
            }
            buf0.append(' ');
            buf1.append(' ');
            buf2.append(' ');
        }
        System.out.println(buf0);
        System.out.println(buf1);
        System.out.println(buf2);
    }

    private DataInputBuffer toDataInputBuffer(final DataOutputBuffer dob) {
        final byte[] bytes = dob.buffer();
        final int size = dob.size();
        return new DataInputBuffer(bytes, 0, size);
    }

    @Test
    public void testIntArray() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(new int[] { 6, 7, 8 });
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(int[].class, o.getClass());
        final int[] array = (int[]) o;
        Assert.assertEquals(array.length, 3);
        Assert.assertEquals(6, array[0]);
        Assert.assertEquals(7, array[1]);
        Assert.assertEquals(8, array[2]);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testEmptyObjectArray() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(new Object[0]);
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object[].class, o.getClass());
        final Object[] array = (Object[]) o;
        Assert.assertEquals(array.length, 0);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testString() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject("hello world");
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(String.class, o.getClass());
        Assert.assertEquals("hello world", o);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testClass() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(Byte.class);
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Byte.class, o);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testLong() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(42L);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Long.class, o.getClass());
        Assert.assertEquals(42L, o);
    }

    @Test
    public void testObjectArrayClassString() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(new Object[] { Byte.class, "hello world" });
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object[].class, o.getClass());
        final Object[] array = (Object[]) o;
        Assert.assertEquals(array.length, 2);
        final Object o0 = array[0];
        Assert.assertNotNull(o0);
        Assert.assertEquals(Class.class, o0.getClass());
        Assert.assertEquals(Byte.class, o0);
        final Object o1 = array[1];
        Assert.assertNotNull(o1);
        Assert.assertEquals(String.class, o1.getClass());
        Assert.assertEquals("hello world", o1);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testArrayListClassString() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final ArrayList al = new ArrayList();
        al.add(Class.class);
        al.add("hello world");
        packer.writeObject(al);
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(ArrayList.class, o.getClass());
        final ArrayList arrayList = (ArrayList) o;
        Assert.assertEquals(arrayList.size(), 2);
        final Object o0 = arrayList.get(0);
        Assert.assertNotNull(o0);
        Assert.assertEquals(Class.class, o0.getClass());
        Assert.assertEquals(Class.class, o0);
        final Object o1 = arrayList.get(1);
        Assert.assertNotNull(o1);
        Assert.assertEquals(String.class, o1.getClass());
        Assert.assertEquals("hello world", o1);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testHashSetClassString() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final HashSet al = new HashSet();
        al.add(Class.class);
        al.add("hello world");
        packer.writeObject(al);
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(HashSet.class, o.getClass());
        final HashSet hashSet = (HashSet) o;
        Assert.assertEquals(hashSet.size(), 2);
        Assert.assertTrue(hashSet.contains(Class.class));
        Assert.assertTrue(hashSet.contains("hello world"));
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testHashMapClassString() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final HashMap al = new HashMap();
        al.put(Class.class, "hello world");
        packer.writeObject(al);
        packer.packer().writeInt(42);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(HashMap.class, o.getClass());
        final HashMap hashMap = (HashMap) o;
        Assert.assertEquals(hashMap.size(), 1);
        Assert.assertTrue(hashMap.containsKey(Class.class));
        Assert.assertTrue(hashMap.containsValue("hello world"));
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    private Object doTestCycle(final Object o) throws IOException {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(o);
        packer.packer().close();
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));
        final Object copy = oui.readObject();
        Assert.assertNotNull(copy);
        Assert.assertEquals(o.getClass(), copy.getClass());
        return copy;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCycles() throws Exception {
        // Horribly knotted object graph ...
        final HashMap map = new HashMap();
        final ArrayList list = new ArrayList();
        final Object[] array = new Object[] { map, list };
        list.add(list);
        list.add(map);
        list.add(array);
        map.put(array, list);

        final HashMap mapCopy = (HashMap) doTestCycle(map);
        Assert.assertEquals(1, mapCopy.size());
        final Object mapKey = mapCopy.keySet().iterator().next();
        Assert.assertNotNull(mapKey);
        Assert.assertEquals(Object[].class, mapKey.getClass());
        final Object[] arrayCopy = (Object[]) mapKey;
        Assert.assertEquals(2, arrayCopy.length);
        final Object mapValue = mapCopy.get(mapKey);
        Assert.assertNotNull(mapValue);
        Assert.assertEquals(ArrayList.class, mapValue.getClass());
        final ArrayList listCopy = (ArrayList) mapValue;
        Assert.assertEquals(3, listCopy.size());
        Assert.assertSame(listCopy.get(0), listCopy);
        Assert.assertSame(listCopy.get(1), mapCopy);
        Assert.assertSame(listCopy.get(2), arrayCopy);
        Assert.assertSame(mapCopy, arrayCopy[0]);
        Assert.assertSame(listCopy, arrayCopy[1]);
    }

    @Test
    public void test2D() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final String[] array1 = new String[] { "hello world" };
        final Integer[] array2 = new Integer[] { 4, 2 };
        packer.writeObject(new Object[][] { array1, array2 });
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object[][].class, o.getClass());
        final Object[][] array = (Object[][]) o;
        Assert.assertEquals(array.length, 2);
        Assert.assertNotNull(array[0]);
        Assert.assertEquals(String[].class, array[0].getClass());
        final String[] array1Copy = (String[]) array[0];
        Assert.assertEquals(array1Copy.length, 1);
        Assert.assertEquals(array1Copy[0], "hello world");
        Assert.assertNotNull(array[1]);
        Assert.assertEquals(Integer[].class, array[1].getClass());
        final Integer[] array2Copy = (Integer[]) array[1];
        Assert.assertEquals(array2Copy.length, 2);
        Assert.assertEquals(array2Copy[0], new Integer(4));
        Assert.assertEquals(array2Copy[1], new Integer(2));
    }

    @Test
    public void test3D() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final String[] array0 = new String[] { "hello world" };
        final CharSequence[][] array1 = new CharSequence[][] { array0 };
        packer.writeObject(new Object[][][] { array1 });
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object[][][].class, o.getClass());
        final Object[][] array = (Object[][][]) o;
        Assert.assertEquals(array.length, 1);
        Assert.assertNotNull(array[0]);
        Assert.assertEquals(CharSequence[][].class, array[0].getClass());
        final CharSequence[][] array1Copy = (CharSequence[][]) array[0];
        Assert.assertEquals(array1Copy.length, 1);
        Assert.assertEquals(String[].class, array1Copy[0].getClass());
        final String[] array0Copy = (String[]) array1Copy[0];
        Assert.assertEquals(array0Copy.length, 1);
        Assert.assertEquals(array0Copy[0], "hello world");
    }

    @Test
    public void testObject() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(new Object());
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object.class, o.getClass());
    }

    @Test
    public void testSlice() throws Exception {
        final byte[] bytes = new byte[] { (byte) 0, (byte) 1, (byte) 2,
                (byte) 3, (byte) 4 };
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        packer.writeObject(bytes, 1, 3);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(byte[].class, o.getClass());
        final byte[] bytesCopy = (byte[]) o;
        Assert.assertEquals(3, bytesCopy.length);
        Assert.assertEquals((byte) 1, bytesCopy[0]);
        Assert.assertEquals((byte) 2, bytesCopy[1]);
        Assert.assertEquals((byte) 3, bytesCopy[2]);
    }

    @Test
    public void testPartial() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final TestRaw tr = new TestRaw();
        tr.value = 42;
        packer.writeObject(tr);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(TestRaw.class, o.getClass());
        final TestRaw trCopy = (TestRaw) o;
        Assert.assertEquals(42, trCopy.value);
    }

    @Test
    public void testFixedSizeArray() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        final ObjectPackerImpl packer = newObjectPacker(dob);
        final TestFixed tf1 = new TestFixed();
        tf1.v1 = true;
        final TestFixed tf2 = new TestFixed();
        tf2.v2 = true;
        packer.writeObject(new TestFixed[] { tf1, tf2 }, false);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(extended));

        final Object o = oui.readObject(false);
        Assert.assertNotNull(o);
        Assert.assertEquals(TestFixed[].class, o.getClass());
        final TestFixed[] array = (TestFixed[]) o;
        Assert.assertEquals(2, array.length);
        Assert.assertNotNull(array[0]);
        Assert.assertNotNull(array[1]);
        Assert.assertTrue(array[0].v1);
        Assert.assertFalse(array[0].v2);
        Assert.assertFalse(array[1].v1);
        Assert.assertTrue(array[1].v2);
        // That is what tells us if the optimization worked:
        // the number of bytes written
        Assert.assertEquals(6, dob.size());
    }
}
