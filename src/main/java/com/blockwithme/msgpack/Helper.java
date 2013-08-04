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

    /** Writes a boolean array. */
    public static void writeBooleanArray(final Packer packer,
            final boolean[] target) throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final boolean a : target) {
            packer.writeBoolean(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a short array. */
    public static void writeShortArray(final Packer packer, final short[] target)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final short a : target) {
            packer.writeShort(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a char array. */
    public static void writeCharArray(final Packer packer, final char[] target)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final char a : target) {
            packer.writeChar(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a int array. */
    public static void writeIntArray(final Packer packer, final int[] target)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final int a : target) {
            packer.writeInt(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a long array. */
    public static void writeLongArray(final Packer packer, final long[] target)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final long a : target) {
            packer.writeLong(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a float array. */
    public static void writeFloatArray(final Packer packer, final float[] target)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final float a : target) {
            packer.writeFloat(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a double array. */
    public static void writeDoubleArray(final Packer packer,
            final double[] target) throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(target.length);
        for (final double a : target) {
            packer.writeDouble(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a boolean array. */
    public static void writeBooleanArray(final Packer packer,
            final boolean[] target, final int offset, final int length)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final boolean a = target[i];
            packer.writeBoolean(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a short array. */
    public static void writeShortArray(final Packer packer,
            final short[] target, final int offset, final int length)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final short a = target[i];
            packer.writeShort(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a char array. */
    public static void writeCharArray(final Packer packer, final char[] target,
            final int offset, final int length) throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final char a = target[i];
            packer.writeChar(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a int array. */
    public static void writeIntArray(final Packer packer, final int[] target,
            final int offset, final int length) throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final int a = target[i];
            packer.writeInt(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a long array. */
    public static void writeLongArray(final Packer packer, final long[] target,
            final int offset, final int length) throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final long a = target[i];
            packer.writeLong(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a float array. */
    public static void writeFloatArray(final Packer packer,
            final float[] target, final int offset, final int length)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final float a = target[i];
            packer.writeFloat(a);
        }
        packer.writeArrayEnd();

    }

    /** Writes a double array. */
    public static void writeDoubleArray(final Packer packer,
            final double[] target, final int offset, final int length)
            throws IOException {
        if (target == null) {
            packer.writeNil();

        }
        packer.writeArrayBegin(length);
        final int end = (offset + length);
        for (int i = offset; i < end; i++) {
            final double a = target[i];
            packer.writeDouble(a);
        }
        packer.writeArrayEnd();

    }

    /** Reads a boolean array. */
    public static boolean[] readBooleanArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final boolean[] result = new boolean[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readBoolean();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a short array. */
    public static short[] readShortArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final short[] result = new short[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readShort();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a char array. */
    public static char[] readCharArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final char[] result = new char[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readChar();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a int array. */
    public static int[] readIntArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readInt();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a long array. */
    public static long[] readLongArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final long[] result = new long[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readLong();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a float array. */
    public static float[] readFloatArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readFloat();
        }
        unpacker.readArrayEnd();
        return result;
    }

    /** Reads a double array. */
    public static double[] readDoubleArray(final Unpacker unpacker)
            throws IOException {
        if (unpacker.trySkipNil()) {
            return null;
        }
        final int n = unpacker.readArrayBegin();
        final double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = unpacker.readDouble();
        }
        unpacker.readArrayEnd();
        return result;
    }
}
