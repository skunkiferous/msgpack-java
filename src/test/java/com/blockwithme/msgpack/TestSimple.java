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
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;

import com.blockwithme.msgpack.impl.MessagePackPacker;
import com.blockwithme.msgpack.impl.MessagePackUnpacker;
import com.blockwithme.msgpack.impl.ObjectPackerImpl;
import com.blockwithme.msgpack.impl.ObjectUnpackerImpl;
import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.UnpackerContext;
import com.blockwithme.util.DataInputBuffer;
import com.blockwithme.util.DataOutputBuffer;

/**
 * @author monster
 *
 */
public class TestSimple {

    private DataOutputBuffer newDataOutputBuffer() {
        return new DataOutputBuffer(2048);
    }

    private MessagePackPacker newPacker() {
        return new MessagePackPacker(newDataOutputBuffer());
    }

    private ObjectPackerImpl newObjectPacker() {
        return new ObjectPackerImpl(newPacker(), new PackerContext(
                BasicTemplates.getAllBasicTemplates()));
    }

    private DataOutputBuffer getDataOutputBuffer(final ObjectPacker op) {
        return (DataOutputBuffer) ((MessagePackPacker) op.packer())
                .getDataOutput();
    }

    private void dumpOP(final ObjectPacker op) {
        dumpOP(getDataOutputBuffer(op));
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

    private DataInputBuffer toDataInputBuffer(final ObjectPacker op) {
        return toDataInputBuffer(getDataOutputBuffer(op));
    }

    private DataInputBuffer toDataInputBuffer(final DataOutputBuffer dob) {
        final byte[] bytes = dob.buffer();
        final int size = dob.size();
        return new DataInputBuffer(bytes, size);
    }

    @Test
    public void testIntArray() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(new int[] { 6, 7, 8 });
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

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
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(new Object[0]);
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Object[].class, o.getClass());
        final Object[] array = (Object[]) o;
        Assert.assertEquals(array.length, 0);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testString() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject("hello world");
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(String.class, o.getClass());
        Assert.assertEquals("hello world", o);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testClass() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(Byte.class);
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Byte.class, o);
        Assert.assertEquals(42, oui.unpacker().readInt());
    }

    @Test
    public void testLong() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(42L);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Long.class, o.getClass());
        Assert.assertEquals(42L, o);
    }

    @Test
    public void testObjectArrayClassString() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(new Object[] { Byte.class, "hello world" });
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

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
        final ObjectPackerImpl packer = newObjectPacker();
        final ArrayList al = new ArrayList();
        al.add(Class.class);
        al.add("hello world");
        packer.writeObject(al);
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

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
        final ObjectPackerImpl packer = newObjectPacker();
        final HashSet al = new HashSet();
        al.add(Class.class);
        al.add("hello world");
        packer.writeObject(al);
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

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
        final ObjectPackerImpl packer = newObjectPacker();
        final HashMap al = new HashMap();
        al.put(Class.class, "hello world");
        packer.writeObject(al);
        packer.packer().write(42);
        packer.packer().close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

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
        final ObjectPackerImpl packer = newObjectPacker();
        packer.writeObject(o);
        packer.packer().close();
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));
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
}
