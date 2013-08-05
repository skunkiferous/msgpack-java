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

import com.blockwithme.msgpack.impl.MessagePackPacker;
import com.blockwithme.msgpack.impl.ObjectPackerImpl;
import com.blockwithme.msgpack.schema.BasicSchemaManager;
import com.blockwithme.msgpack.schema.SchemaManager;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.util.DataInputBuffer;
import com.blockwithme.util.DataOutputBuffer;

/**
 * @author monster
 *
 */
public abstract class BaseTest {

    protected SchemaManager newSchemaManager(final int schema) {
        return new BasicSchemaManager(extended(schema)) {
            @Override
            protected int getBasicTemplateCount(final int schemaID) {
                return 28;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    protected abstract Template[] extended(final int schema);

    protected DataOutputBuffer newDataOutputBuffer() {
        return new DataOutputBuffer(2048);
    }

    protected MessagePackPacker newPacker(final DataOutputBuffer dob) {
        return new MessagePackPacker(dob);
    }

    protected ObjectPackerImpl newObjectPacker(final DataOutputBuffer dob)
            throws IOException {
        return newObjectPacker(dob, 42);
    }

    protected ObjectPackerImpl newObjectPacker(final DataOutputBuffer dob,
            final int schema) throws IOException {
        final PackerContext pc = new PackerContext(new BasicSchemaManager(
                extended(schema)) {
            @Override
            protected int getBasicTemplateCount(final int schemaID) {
                return 28;
            }
        });
        pc.schemaID = schema;
        return new ObjectPackerImpl(newPacker(dob), pc);
    }

    protected void dumpOP(final DataOutputBuffer dob) {
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

    protected DataInputBuffer toDataInputBuffer(final DataOutputBuffer dob) {
        final byte[] bytes = dob.buffer();
        final int size = dob.size();
        return new DataInputBuffer(bytes, 0, size);
    }
}
