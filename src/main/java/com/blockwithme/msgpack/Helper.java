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

import java.io.DataOutput;
import java.io.IOException;

import com.blockwithme.msgpack.impl.MessagePackPacker;
import com.blockwithme.msgpack.impl.MessagePackUnpacker;
import com.blockwithme.msgpack.impl.ObjectPackerImpl;
import com.blockwithme.msgpack.impl.ObjectUnpackerImpl;
import com.blockwithme.msgpack.templates.PackerContext;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates.UnpackerContext;
import com.blockwithme.util.DataInputBuffer;

/**
 * Helper class for the MessagePack API.
 *
 * @author monster
 */
public class Helper {
    /** The format and schema versions. */
    public static final class Version {
        /** The format version. */
        public final int format;
        /** The schema version. */
        public final int schema;

        /** Constructor */
        public Version(final int format, final int schema) {
            this.format = format;
            this.schema = schema;
        }

        /** toString */
        @Override
        public String toString() {
            return "Version(" + format + "," + schema + ")";
        }
    }

    /** Creates a new packer. */
    public static Packer newPacker(final DataOutput out) {
        return new MessagePackPacker(out);
    }

    /** Creates a new object packer.
     * @throws IOException */
    public static ObjectPacker newObjectPacker(final DataOutput out,
            final PackerContext context) throws IOException {
        return new ObjectPackerImpl(newPacker(out), context);
    }

    /** Creates a new object packer.
     * @throws IOException */
    public static ObjectPacker newObjectPacker(final DataOutput out,
            final Template<?>[] idToTemplate, final int schema)
            throws IOException {
        return newObjectPacker(out, new PackerContext(idToTemplate, schema));
    }

    /** Creates a new Unpacker for the bytes. */
    public static Unpacker newUnpacker(final byte[] bytes) {
        return new MessagePackUnpacker(new DataInputBuffer(bytes));
    }

    /** Creates a new ObjectUnpacker for the bytes.
     * @throws IOException */
    public static ObjectUnpacker newObjectUnpacker(final byte[] bytes,
            final UnpackerContext context) throws IOException {
        return new ObjectUnpackerImpl(new MessagePackUnpacker(
                new DataInputBuffer(bytes)), context);
    }

    /** Creates a new ObjectUnpacker for the bytes.
     * @throws IOException */
    public static ObjectUnpacker newObjectUnpacker(final byte[] bytes,
            final Template<?>[] idToTemplate) throws IOException {
        return newObjectUnpacker(bytes, new UnpackerContext(idToTemplate));
    }

    /** Returns the format version, and the schema version, for the given byte array.
     * @throws IOException */
    public static Version getVersion(final byte[] bytes) throws IOException {
        final Unpacker u = newUnpacker(bytes);
        final int format = u.readIndex();
        final int schema = u.readIndex();
        return new Version(format, schema);
    }
}
