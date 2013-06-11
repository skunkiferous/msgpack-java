/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.blockwithme.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;

// Adapter from DataOutputStream from Apache Harmony

/**
 * Wraps an existing {@link OutputStream} and writes typed data to it.
 * Typically, this stream can be read in by DataInputStream. Types that can be
 * written include byte, 16-bit short, 32-bit int, 32-bit float, 64-bit long,
 * 64-bit double, byte strings, and {@link DataInput MUTF-8} encoded strings.
 *
 * @see DataInputStream
 */
public class DataOutputBuffer implements DataOutput {

    /**
     * The number of bytes written out so far.
     */
    private int written;
    private byte buff[];

    /* Can the buffer handle @i more bytes, if not expand it */
    private void expand(final int i) {
        if (written + i <= buff.length) {
            return;
        }

        final byte[] newbuf = new byte[(written + i) * 2];
        System.arraycopy(buff, 0, newbuf, 0, written);
        buff = newbuf;
    }

    /** Write one byte. Grows if needed. */
    private void _write(final byte oneByte) {
        buff[written++] = oneByte;
    }

    /**
     * Constructs a new {@code DataOutputStream} on the {@code OutputStream}
     * {@code out}. Note that data written by this stream is not in a human
     * readable form but can be reconstructed by using a {@link DataInputStream}
     * on the resulting output.
     *
     * @param out
     *            the target stream for writing.
     */
    public DataOutputBuffer(final int capacity) {
        buff = new byte[capacity];
    }

    /** Returns the buffer. */
    public final byte[] buffer() {
        return buff;
    }

    /** Reset the index to 0. */
    public final void reset() {
        written = 0;
    }

    /**
     * Returns the total number of bytes written to the target stream so far.
     *
     * @return the number of bytes written to the target stream.
     */
    public final int size() {
        return written;
    }

    /**
     * Writes {@code count} bytes from the byte array {@code buffer} starting at
     * {@code offset} to the target stream.
     *
     * @param buffer
     *            the buffer to write to the target stream.
     * @param offset
     *            the index of the first byte in {@code buffer} to write.
     * @param count
     *            the number of bytes from the {@code buffer} to write.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @throws NullPointerException
     *             if {@code buffer} is {@code null}.
     * @see DataInputStream#readFully(byte[])
     * @see DataInputStream#readFully(byte[], int, int)
     */
    @Override
    public void write(final byte buffer[], final int offset, final int count)
            throws IOException {
        expand(count);
        System.arraycopy(buffer, offset, buff, written, count);
        written += count;
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Writes a byte to the target stream. Only the least significant byte of
     * the integer {@code oneByte} is written.
     *
     * @param oneByte
     *            the byte to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readByte()
     */
    @Override
    public void write(final int oneByte) throws IOException {
        expand(1);
        _write((byte) oneByte);
    }

    /**
     * Writes a boolean to the target stream.
     *
     * @param val
     *            the boolean value to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readBoolean()
     */
    @Override
    public final void writeBoolean(final boolean val) throws IOException {
        expand(1);
        _write((byte) (val ? 1 : 0));
    }

    /**
     * Writes an 8-bit byte to the target stream. Only the least significant
     * byte of the integer {@code val} is written.
     *
     * @param val
     *            the byte value to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readByte()
     * @see DataInputStream#readUnsignedByte()
     */
    @Override
    public final void writeByte(final int val) throws IOException {
        expand(1);
        _write((byte) val);
    }

    /**
     * Writes the low order bytes from a string to the target stream.
     *
     * @param str
     *            the string containing the bytes to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readFully(byte[])
     * @see DataInputStream#readFully(byte[],int,int)
     */
    @Override
    public final void writeBytes(final String str) throws IOException {
        final int len = str.length();
        expand(len);
        for (int index = 0; index < len; index++) {
            _write((byte) str.charAt(index));
        }
    }

    /**
     * Writes a 16-bit character to the target stream. Only the two lower bytes
     * of the integer {@code val} are written, with the higher one written
     * first. This corresponds to the Unicode value of {@code val}.
     *
     * @param val
     *            the character to write to the target stream
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readChar()
     */
    @Override
    public final void writeChar(final int val) throws IOException {
        expand(2);
        _write((byte) (val >> 8));
        _write((byte) val);
    }

    /**
     * Writes the 16-bit characters contained in {@code str} to the target
     * stream.
     *
     * @param str
     *            the string that contains the characters to write to this
     *            stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readChar()
     */
    @Override
    public final void writeChars(final String str) throws IOException {
        final int len = str.length();
        expand(len * 2);
        for (int index = 0; index < len; index++) {
            final char c = str.charAt(index);
            _write((byte) (c >> 8));
            _write((byte) c);
        }
    }

    /**
     * Writes a 64-bit double to the target stream. The resulting output is the
     * eight bytes resulting from calling Double.doubleToLongBits().
     *
     * @param val
     *            the double to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readDouble()
     */
    @Override
    public final void writeDouble(final double val) throws IOException {
        writeLong(Double.doubleToRawLongBits(val));
    }

    /**
     * Writes a 32-bit float to the target stream. The resulting output is the
     * four bytes resulting from calling Float.floatToIntBits().
     *
     * @param val
     *            the float to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readFloat()
     */
    @Override
    public final void writeFloat(final float val) throws IOException {
        writeInt(Float.floatToRawIntBits(val));
    }

    /**
     * Writes a 32-bit int to the target stream. The resulting output is the
     * four bytes, highest order first, of {@code val}.
     *
     * @param val
     *            the int to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readInt()
     */
    @Override
    public final void writeInt(final int val) throws IOException {
        expand(4);
        _write((byte) (val >> 24));
        _write((byte) (val >> 16));
        _write((byte) (val >> 8));
        _write((byte) val);
    }

    /**
     * Writes a 64-bit long to the target stream. The resulting output is the
     * eight bytes, highest order first, of {@code val}.
     *
     * @param val
     *            the long to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readLong()
     */
    @Override
    public final void writeLong(final long val) throws IOException {
        expand(8);
        _write((byte) (val >> 56));
        _write((byte) (val >> 48));
        _write((byte) (val >> 40));
        _write((byte) (val >> 32));
        _write((byte) (val >> 24));
        _write((byte) (val >> 16));
        _write((byte) (val >> 8));
        _write((byte) val);
    }

    /**
     * Writes the specified 16-bit short to the target stream. Only the lower
     * two bytes of the integer {@code val} are written, with the higher one
     * written first.
     *
     * @param val
     *            the short to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readShort()
     * @see DataInputStream#readUnsignedShort()
     */
    @Override
    public final void writeShort(final int val) throws IOException {
        expand(2);
        _write((byte) (val >> 8));
        _write((byte) val);
    }

    /**
     * Writes the specified encoded in {@link DataInput modified UTF-8} to this
     * stream.
     *
     * @param str
     *            the string to write to the target stream encoded in
     *            {@link DataInput modified UTF-8}.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @throws UTFDataFormatException
     *             if the encoded string is longer than 65535 bytes.
     * @see DataInputStream#readUTF()
     */
    @Override
    public final void writeUTF(final String str) throws IOException {
        final int length = str.length();
        if (length > 65535) {
            throw new UTFDataFormatException("string too big");
        }
        expand(2 + length * 2);
        int utfCount = 0;
        // Skip space for size. Write it later
        written += 2;
        final int start = written;
        for (int i = 0; i < length; i++) {
            final int charValue = str.charAt(i);
            if (charValue > 0 && charValue <= 127) {
                expand(1);
                _write((byte) charValue);
            } else if (charValue <= 2047) {
                expand(2);
                _write((byte) (0xc0 | (0x1f & (charValue >> 6))));
                _write((byte) (0x80 | (0x3f & charValue)));
            } else {
                expand(3);
                _write((byte) (0xe0 | (0x0f & (charValue >> 12))));
                _write((byte) (0x80 | (0x3f & (charValue >> 6))));
                _write((byte) (0x80 | (0x3f & charValue)));
            }
            utfCount = written - start;
            if (utfCount > 65535) {
                throw new UTFDataFormatException("string too big");
            }
        }
        final int end = written;
        written = start - 2;
        _write((byte) (utfCount >> 8));
        _write((byte) utfCount);
        written = end;
    }
}
