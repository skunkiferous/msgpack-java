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

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

// Adapted from Apache Harmony DataInputStream

/**
 * Wraps an existing {@link InputStream} and reads typed data from it.
 * Typically, this stream has been written by a DataOutputStream. Types that can
 * be read include byte, 16-bit short, 32-bit int, 32-bit float, 64-bit long,
 * 64-bit double, byte strings, and strings encoded in
 * {@link DataInput modified UTF-8}.
 *
 * @see DataOutputStream
 */
public class DataInputBuffer implements DataInput {

    private final byte[] buff;

    private final int length;

    private int read;

    private void require(final int count) throws IOException {
        if (read + count > length) {
            throw new EOFException();
        }
    }

    /**
     * Constructs a new DataInputStream on the InputStream {@code in}. All
     * reads are then filtered through this stream. Note that data read by this
     * stream is not in a human readable format and was most likely created by a
     * DataOutputStream.
     *
     * @param in
     *            the source InputStream the filter reads from.
     * @see DataOutputStream
     * @see RandomAccessFile
     */
    public DataInputBuffer(final byte[] buff) {
        this(buff, 0, buff.length);
    }

    /**
     * Constructs a new DataInputStream on the InputStream {@code in}. All
     * reads are then filtered through this stream. Note that data read by this
     * stream is not in a human readable format and was most likely created by a
     * DataOutputStream.
     *
     * @param in
     *            the source InputStream the filter reads from.
     * @see DataOutputStream
     * @see RandomAccessFile
     */
    public DataInputBuffer(final byte[] buff, final int offset, final int length) {
        if (buff == null) {
            throw new NullPointerException();
        }
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length > buff.length) {
            throw new IllegalArgumentException("length: " + length
                    + " > buff.length: " + buff.length);
        }
        this.buff = buff;
        this.read = offset;
        this.length = length;
    }

    /**
     * Reads bytes from this stream into the byte array {@code buffer}. Returns
     * the number of bytes that have been read.
     *
     * @param buffer
     *            the buffer to read bytes into.
     * @return the number of bytes that have been read or -1 if the end of the
     *         stream has been reached.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#write(byte[])
     * @see DataOutput#write(byte[], int, int)
     */
    public final int read(final byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    /**
     * Reads at most {@code length} bytes from this stream and stores them in
     * the byte array {@code buffer} starting at {@code offset}. Returns the
     * number of bytes that have been read or -1 if no bytes have been read and
     * the end of the stream has been reached.
     *
     * @param buffer
     *            the byte array in which to store the bytes read.
     * @param offset
     *            the initial position in {@code buffer} to store the bytes
     *            read from this stream.
     * @param length
     *            the maximum number of bytes to store in {@code buffer}.
     * @return the number of bytes that have been read or -1 if the end of the
     *         stream has been reached.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#write(byte[])
     * @see DataOutput#write(byte[], int, int)
     */
    public final int read(final byte[] buffer, final int offset,
            final int length) throws IOException {
        final int left = this.length - read;
        if (left == 0) {
            return -1;
        }
        if (left >= length) {
            System.arraycopy(buff, read, buffer, offset, length);
            read += length;
            return length;
        }
        System.arraycopy(buff, read, buffer, offset, left);
        read = this.length;
        return left;
    }

    /**
     * Reads a boolean from this stream.
     *
     * @return the next boolean value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before one byte
     *             has been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeBoolean(boolean)
     */
    @Override
    public final boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    /**
     * Reads an 8-bit byte value from this stream.
     *
     * @return the next byte value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before one byte
     *             has been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeByte(int)
     */
    @Override
    public final byte readByte() throws IOException {
        require(1);
        return buff[read++];
    }

    @Override
    public final char readChar() throws IOException {
        require(2);
        return (char) (((buff[read++] & 0xff) << 8) | (buff[read++] & 0xff));

    }

    /**
     * Reads a 64-bit double value from this stream.
     *
     * @return the next double value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before eight
     *             bytes have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeDouble(double)
     */
    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads a 32-bit float value from this stream.
     *
     * @return the next float value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before four
     *             bytes have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeFloat(float)
     */
    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads bytes from this stream into the byte array {@code buffer}. This
     * method will block until {@code buffer.length} number of bytes have been
     * read.
     *
     * @param buffer
     *            to read bytes into.
     * @throws EOFException
     *             if the end of the source stream is reached before enough
     *             bytes have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#write(byte[])
     * @see DataOutput#write(byte[], int, int)
     */
    @Override
    public final void readFully(final byte[] buffer) throws IOException {
        readFully(buffer, 0, buffer.length);
    }

    /**
     * Reads bytes from this stream and stores them in the byte array {@code
     * buffer} starting at the position {@code offset}. This method blocks until
     * {@code length} bytes have been read. If {@code length} is zero, then this
     * method returns without reading any bytes.
     *
     * @param buffer
     *            the byte array into which the data is read.
     * @param offset
     *            the offset in {@code buffer} from where to store the bytes
     *            read.
     * @param length
     *            the maximum number of bytes to read.
     * @throws EOFException
     *             if the end of the source stream is reached before enough
     *             bytes have been read.
     * @throws IndexOutOfBoundsException
     *             if {@code offset < 0} or {@code length < 0}, or if {@code
     *             offset + length} is greater than the size of {@code buffer}.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @throws NullPointerException
     *             if {@code buffer} or the source stream are null.
     * @see java.io.DataInput#readFully(byte[], int, int)
     */
    @Override
    public final void readFully(final byte[] buffer, int offset, int length)
            throws IOException {
        if (length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (length == 0) {
            return;
        }
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (offset < 0 || offset > buffer.length - length) {
            throw new IndexOutOfBoundsException();
        }
        while (length > 0) {
            final int result = read(buffer, offset, length);
            if (result < 0) {
                throw new EOFException();
            }
            offset += result;
            length -= result;
        }
    }

    /**
     * Reads a 32-bit integer value from this stream.
     *
     * @return the next int value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before four
     *             bytes have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeInt(int)
     */
    @Override
    public final int readInt() throws IOException {
        require(4);
        return ((buff[read++] & 0xff) << 24) | ((buff[read++] & 0xff) << 16)
                | ((buff[read++] & 0xff) << 8) | (buff[read++] & 0xff);
    }

    /**
     * Returns a string that contains the next line of text available from the
     * source stream. A line is represented by zero or more characters followed
     * by {@code '\n'}, {@code '\r'}, {@code "\r\n"} or the end of the stream.
     * The string does not include the newline sequence.
     *
     * @return the contents of the line or {@code null} if no characters were
     *         read before the end of the source stream has been reached.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @deprecated Use {@link BufferedReader}
     */
    @Override
    @Deprecated
    public final String readLine() throws IOException {
        throw new UnsupportedOperationException("deprecated!");
    }

    /**
     * Reads a 64-bit long value from this stream.
     *
     * @return the next long value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before eight
     *             bytes have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeLong(long)
     */
    @Override
    public final long readLong() throws IOException {
        require(8);
        final int i1 = ((buff[read++] & 0xff) << 24)
                | ((buff[read++] & 0xff) << 16) | ((buff[read++] & 0xff) << 8)
                | (buff[read++] & 0xff);
        final int i2 = ((buff[read++] & 0xff) << 24)
                | ((buff[read++] & 0xff) << 16) | ((buff[read++] & 0xff) << 8)
                | (buff[read++] & 0xff);

        return ((i1 & 0xffffffffL) << 32) | (i2 & 0xffffffffL);
    }

    /**
     * Reads a 16-bit short value from this stream.
     *
     * @return the next short value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before two bytes
     *             have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeShort(int)
     */
    @Override
    public final short readShort() throws IOException {
        require(2);
        return (short) (((buff[read++] & 0xff) << 8) | (buff[read++] & 0xff));
    }

    /**
     * Reads an unsigned 8-bit byte value from this stream and returns it as an
     * int.
     *
     * @return the next unsigned byte value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream has been reached before one
     *             byte has been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeByte(int)
     */
    @Override
    public final int readUnsignedByte() throws IOException {
        require(2);
        return buff[read++] & 0xFF;
    }

    /**
     * Reads a 16-bit unsigned short value from this stream and returns it as an
     * int.
     *
     * @return the next unsigned short value from the source stream.
     * @throws EOFException
     *             if the end of the filtered stream is reached before two bytes
     *             have been read.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeShort(int)
     */
    @Override
    public final int readUnsignedShort() throws IOException {
        require(2);
        return (char) (((buff[read++] & 0xff) << 8) | (buff[read++] & 0xff));
    }

    /**
     * Reads an string encoded in {@link DataInput modified UTF-8} from this
     * stream.
     *
     * @return the next {@link DataInput MUTF-8} encoded string read from the
     *         source stream.
     * @throws EOFException if the end of the input is reached before the read
     *         request can be satisfied.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#writeUTF(java.lang.String)
     */
    @Override
    public final String readUTF() throws IOException {
        final int utfSize = readUnsignedShort();
        final byte[] buf = new byte[utfSize];
        final char[] out = new char[utfSize];
        readFully(buf, 0, utfSize);
        return convertUTF8WithBuf(buf, out, 0, utfSize);
    }

    /**
     * Skips {@code count} number of bytes in this stream. Subsequent {@code
     * read()}s will not return these bytes unless {@code reset()} is used.
     *
     * This method will not throw an {@link EOFException} if the end of the
     * input is reached before {@code count} bytes where skipped.
     *
     * @param count
     *            the number of bytes to skip.
     * @return the number of bytes actually skipped.
     * @throws IOException
     *             if a problem occurs during skipping.
     * @see #mark(int)
     * @see #reset()
     */
    @Override
    public final int skipBytes(final int count) throws IOException {
        final int left = length - read;
        if (left >= count) {
            read += count;
            return count;
        }
        read = length;
        return left;
    }

    /** FROM: org.apache.harmony.luni.util.Util */
    public static String convertUTF8WithBuf(final byte[] buf, final char[] out,
            final int offset, final int utfSize) throws IOException {
        int count = 0, s = 0, a;
        while (count < utfSize) {
            if ((out[s] = (char) buf[offset + count++]) < '\u0080')
                s++;
            else if (((a = out[s]) & 0xe0) == 0xc0) {
                if (count >= utfSize)
                    throw new IOException();
                final int b = buf[count++];
                if ((b & 0xC0) != 0x80)
                    throw new IOException();
                out[s++] = (char) (((a & 0x1F) << 6) | (b & 0x3F));
            } else if ((a & 0xf0) == 0xe0) {
                if (count + 1 >= utfSize)
                    throw new IOException();
                final int b = buf[count++];
                final int c = buf[count++];
                if (((b & 0xC0) != 0x80) || ((c & 0xC0) != 0x80))
                    throw new IOException();
                out[s++] = (char) (((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F));
            } else {
                throw new IOException();
            }
        }
        return new String(out, 0, s);
    }
}
