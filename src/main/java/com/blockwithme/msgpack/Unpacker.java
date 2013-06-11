//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package com.blockwithme.msgpack;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Standard deserializer.
 */
public interface Unpacker extends Closeable {

    public void skip() throws IOException;

    public int readArrayBegin() throws IOException;

    public void readArrayEnd(final boolean check) throws IOException;

    public void readArrayEnd() throws IOException;

    public int readMapBegin() throws IOException;

    public void readMapEnd(final boolean check) throws IOException;

    public void readMapEnd() throws IOException;

    public void readNil() throws IOException;

    public boolean trySkipNil() throws IOException;

    public boolean readBoolean() throws IOException;

    public byte readByte() throws IOException;

    public short readShort() throws IOException;

    public char readChar() throws IOException;

    public int readInt() throws IOException;

    public long readLong() throws IOException;

    public BigInteger readBigInteger() throws IOException;

    public float readFloat() throws IOException;

    public double readDouble() throws IOException;

    public byte[] readByteArray() throws IOException;

    public boolean[] readBooleanArray() throws IOException;

    public short[] readShortArray() throws IOException;

    public char[] readCharArray() throws IOException;

    public int[] readIntArray() throws IOException;

    public long[] readLongArray() throws IOException;

    public float[] readFloatArray() throws IOException;

    public double[] readDoubleArray() throws IOException;

    public ByteBuffer readByteBuffer() throws IOException;

    public String readString() throws IOException;

    public ValueType getNextType() throws IOException;

    public void setRawSizeLimit(final int size);

    public void setArraySizeLimit(final int size);

    public void setMapSizeLimit(final int size);
}
