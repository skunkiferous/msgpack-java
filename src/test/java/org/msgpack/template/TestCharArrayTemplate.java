package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;

public class TestCharArrayTemplate {

    @Test
    public void testPackUnpack00() throws Exception {
        new TestPackUnpack(0).testCharArray();
    }

    @Test
    public void testPackUnpack01() throws Exception {
        new TestPackUnpack(1).testCharArray();
    }

    @Test
    public void testPackUnpack02() throws Exception {
        new TestPackUnpack(2).testCharArray();
    }

    @Test
    public void testPackBufferUnpack00() throws Exception {
        new TestPackBufferUnpack(0).testCharArray();
    }

    @Test
    public void testPackBufferUnpack01() throws Exception {
        new TestPackBufferUnpack(1).testCharArray();
    }

    @Test
    public void testPackBufferUnpack02() throws Exception {
        new TestPackBufferUnpack(2).testCharArray();
    }

    @Test
    public void testBufferPackBufferUnpack00() throws Exception {
        new TestBufferPackBufferUnpack(0).testCharArray();
    }

    @Test
    public void testBufferPackBufferUnpack01() throws Exception {
        new TestBufferPackBufferUnpack(1).testCharArray();
    }

    @Test
    public void testBufferPackBufferUnpack02() throws Exception {
        new TestBufferPackBufferUnpack(2).testCharArray();
    }

    @Test
    public void testBufferPackUnpack00() throws Exception {
        new TestBufferPackUnpack(0).testCharArray();
    }

    @Test
    public void testBufferPackUnpack01() throws Exception {
        new TestBufferPackUnpack(1).testCharArray();
    }

    @Test
    public void testBufferPackUnpack02() throws Exception {
        new TestBufferPackUnpack(2).testCharArray();
    }

    private static class TestPackUnpack extends TestSet {
        private final int index;

        TestPackUnpack(final int i) {
            index = i;
        }

        @Test
        @Override
        public void testCharArray() throws Exception {
            super.testCharArray();
        }

        @Override
        public void testCharArray(final char[] v) throws Exception {
            final MessagePack msgpack = new MessagePack();
            final Template<char[]> tmpl = CharArrayTemplate.instance;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Packer packer = msgpack.createPacker(out);
            tmpl.write(packer, v);
            final byte[] bytes = out.toByteArray();
            final Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.resetReadByteCount();
            char[] ret0;
            switch (index) {
            case 0:
                ret0 = null;
                break;
            case 1:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length];
                }
                break;
            case 2:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length / 2];
                }
                break;
            default:
                throw new IllegalArgumentException();
            }
            final char[] ret = tmpl.read(unpacker, ret0);
            assertCharArrayEquals(v, ret);
            assertEquals(bytes.length, unpacker.getReadByteCount());
        }
    }

    private static class TestPackBufferUnpack extends TestSet {
        private final int index;

        TestPackBufferUnpack(final int i) {
            index = i;
        }

        @Test
        @Override
        public void testCharArray() throws Exception {
            super.testCharArray();
        }

        @Override
        public void testCharArray(final char[] v) throws Exception {
            final MessagePack msgpack = new MessagePack();
            final Template<char[]> tmpl = CharArrayTemplate.instance;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Packer packer = msgpack.createPacker(out);
            tmpl.write(packer, v);
            final byte[] bytes = out.toByteArray();
            final BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
            unpacker.resetReadByteCount();
            char[] ret0;
            switch (index) {
            case 0:
                ret0 = null;
                break;
            case 1:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length];
                }
                break;
            case 2:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length / 2];
                }
                break;
            default:
                throw new IllegalArgumentException();
            }
            final char[] ret = tmpl.read(unpacker, ret0);
            assertCharArrayEquals(v, ret);
            assertEquals(bytes.length, unpacker.getReadByteCount());
        }
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
        private final int index;

        TestBufferPackBufferUnpack(final int i) {
            index = i;
        }

        @Test
        @Override
        public void testCharArray() throws Exception {
            super.testCharArray();
        }

        @Override
        public void testCharArray(final char[] v) throws Exception {
            final MessagePack msgpack = new MessagePack();
            final Template<char[]> tmpl = CharArrayTemplate.instance;
            final BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            final byte[] bytes = packer.toByteArray();
            final BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
            unpacker.resetReadByteCount();
            char[] ret0;
            switch (index) {
            case 0:
                ret0 = null;
                break;
            case 1:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length];
                }
                break;
            case 2:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length / 2];
                }
                break;
            default:
                throw new IllegalArgumentException();
            }
            final char[] ret = tmpl.read(unpacker, ret0);
            assertCharArrayEquals(v, ret);
            assertEquals(bytes.length, unpacker.getReadByteCount());
        }
    }

    private static class TestBufferPackUnpack extends TestSet {
        private final int index;

        TestBufferPackUnpack(final int i) {
            index = i;
        }

        @Test
        @Override
        public void testCharArray() throws Exception {
            super.testCharArray();
        }

        @Override
        public void testCharArray(final char[] v) throws Exception {
            final MessagePack msgpack = new MessagePack();
            final Template<char[]> tmpl = CharArrayTemplate.instance;
            final BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            final byte[] bytes = packer.toByteArray();
            final Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.resetReadByteCount();
            char[] ret0;
            switch (index) {
            case 0:
                ret0 = null;
                break;
            case 1:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length];
                }
                break;
            case 2:
                if (v == null) {
                    ret0 = new char[0];
                } else {
                    ret0 = new char[v.length / 2];
                }
                break;
            default:
                throw new IllegalArgumentException();
            }
            final char[] ret = tmpl.read(unpacker, ret0);
            assertCharArrayEquals(v, ret);
            assertEquals(bytes.length, unpacker.getReadByteCount());
        }
    }

    public static void assertCharArrayEquals(final char[] v, final char[] ret) {
        if (v == null) {
            assertEquals(null, ret);
            return;
        }
        assertEquals(v.length, ret.length);
        for (int i = 0; i < v.length; ++i) {
            assertEquals(v[i], ret[i]);
        }
    }
}
