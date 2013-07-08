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
        final DataOutputBuffer dob = getDataOutputBuffer(op);
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
        packer.write(new int[] { 6, 7, 8 });
        packer.write(42);
        packer.close();
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
        Assert.assertEquals(42, oui.readInt());
    }

    @Test
    public void testEmptyObjectArray() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.write(new Object[0]);
        packer.write(42);
        packer.close();
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
        Assert.assertEquals(42, oui.readInt());
    }

    @Test
    public void testString() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.write("hello world");
        packer.write(42);
        packer.close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(String.class, o.getClass());
        Assert.assertEquals("hello world", o);
        Assert.assertEquals(42, oui.readInt());
    }

    @Test
    public void testClass() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.write(Byte.class);
        packer.write(42);
        packer.close();
        dumpOP(packer);
        final DataInputBuffer dib = toDataInputBuffer(packer);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(BasicTemplates.getAllBasicTemplates()));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(Class.class, o.getClass());
        Assert.assertEquals(Byte.class, o);
        Assert.assertEquals(42, oui.readInt());
    }

    @Test
    public void testObjectArrayClassString() throws Exception {
        final ObjectPackerImpl packer = newObjectPacker();
        packer.write(new Object[] { Byte.class, "hello world" });
        packer.write(42);
        packer.close();
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
        Assert.assertEquals(42, oui.readInt());
    }
}
