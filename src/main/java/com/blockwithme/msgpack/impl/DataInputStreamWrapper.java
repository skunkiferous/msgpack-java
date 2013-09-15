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
package com.blockwithme.msgpack.impl;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Wraps an DataInput into an InputStream.
 *
 * @author monster
 */
public class DataInputStreamWrapper extends InputStream {
    /** The DataInput */
    private final DataInput dataInput;

    /** Wraps a DataInput */
    public DataInputStreamWrapper(final DataInput dataInput) {
        this.dataInput = Objects.requireNonNull(dataInput);
    }

    @Override
    public int read() throws IOException {
        return dataInput.readByte();
    }

    @Override
    public int read(final byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(final byte b[], final int off, final int len)
            throws IOException {
        dataInput.readFully(b, off, len);
        return len;
    }

    @Override
    public long skip(final long n) throws IOException {
        if (n > Integer.MAX_VALUE) {
            return dataInput.skipBytes(Integer.MAX_VALUE);
        }
        return dataInput.skipBytes((int) n);
    }

    @Override
    public void close() throws IOException {
        if (dataInput instanceof Closeable) {
            ((Closeable) dataInput).close();
        }
    }
}
