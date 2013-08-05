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

import junit.framework.Assert;

import org.junit.Test;

import com.blockwithme.msgpack.impl.MessagePackUnpacker;
import com.blockwithme.msgpack.impl.ObjectPackerImpl;
import com.blockwithme.msgpack.impl.ObjectUnpackerImpl;
import com.blockwithme.msgpack.templates.AbstractTemplate;
import com.blockwithme.msgpack.templates.ObjectType;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates.TrackingType;
import com.blockwithme.msgpack.templates.UnpackerContext;
import com.blockwithme.util.DataInputBuffer;
import com.blockwithme.util.DataOutputBuffer;

/**
 * Tests the possibility of doing manual migrations.
 *
 * @author monster
 */
public class TestManualMigration extends BaseTest {
    /** A "raw" object */
    private static class TestMig {
        public int v1;
        // Added in schema version 42 ... We pretend it doesn't exist in v10
        public int v2;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Template[] extended(final int schema) {
        if (schema == 10) {
            return new Template[] { new AbstractTemplate<TestMig>(null,
                    TestMig.class, 1, ObjectType.ARRAY, TrackingType.IDENTITY,
                    1) {

                @Override
                public void writeData(final PackerContext context,
                        final int size, final TestMig v) throws IOException {
                    if (context.getSchema().schema != 10) {
                        throw new IllegalStateException("Expected schema 10!");
                    }
                    final Packer p = context.packer;
                    p.writeInt(v.v1);
                    // v2 doesn't exist yet
//                    p.writeInt(v.v2);
                }

                @Override
                public TestMig readData(final UnpackerContext context,
                        final TestMig preCreated, final int size)
                        throws IOException {
                    if (context.getSchema().schema != 10) {
                        throw new IllegalStateException("Expected schema 10!");
                    }
                    final Unpacker u = context.unpacker;
                    final TestMig result = new TestMig();
                    result.v1 = u.readInt();
                    // v2 doesn't exist yet
//                    result.v2 = u.readInt();
                    return result;
                }
            } };
        }
        // Else ...
        return new Template[] { new AbstractTemplate<TestMig>(null,
                TestMig.class, 1, ObjectType.ARRAY, TrackingType.IDENTITY, 2) {

            @Override
            public void writeData(final PackerContext context, final int size,
                    final TestMig v) throws IOException {
                if (context.getSchema().schema != 42) {
                    throw new IllegalStateException("Expected schema 42!");
                }
                final Packer p = context.packer;
                p.writeInt(v.v1);
                p.writeInt(v.v2);
            }

            @Override
            public TestMig readData(final UnpackerContext context,
                    final TestMig preCreated, final int size)
                    throws IOException {
                final Unpacker u = context.unpacker;
                final TestMig result = new TestMig();
                result.v1 = u.readInt();
                if (context.getSchema().schema == 10) {
                    result.v2 = -1;
                } else if (context.getSchema().schema == 42) {
                    result.v2 = u.readInt();
                } else {
                    throw new IllegalStateException("Expected schema 42!");
                }
                return result;
            }
        } };
    }

    @Test
    public void testMig() throws Exception {
        final DataOutputBuffer dob = newDataOutputBuffer();
        // Save with old schema 10
        final ObjectPackerImpl packer = newObjectPacker(dob, 10);
        final TestMig tr = new TestMig();
        tr.v1 = 42;
        packer.writeObject(tr);
        packer.packer().close();
        dumpOP(dob);
        final DataInputBuffer dib = toDataInputBuffer(dob);
        final MessagePackUnpacker mpu = new MessagePackUnpacker(dib);
        // read with new schema 42
        final ObjectUnpackerImpl oui = new ObjectUnpackerImpl(mpu,
                new UnpackerContext(newSchemaManager(42)));

        final Object o = oui.readObject();
        Assert.assertNotNull(o);
        Assert.assertEquals(TestMig.class, o.getClass());
        final TestMig trCopy = (TestMig) o;
        Assert.assertEquals(42, trCopy.v1);
        Assert.assertEquals(-1, trCopy.v2);
    }
}
