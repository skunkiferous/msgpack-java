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
import java.io.DataOutput;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * DataOutputWrapperStream is an OutputStream that wraps a DataOutput.
 *
 * @author monster
 */
public class DataOutputWrapperStream extends OutputStream {

    /** The DataOutput */
    private final DataOutput dataOutput;

    /** Wraps a DataOutput. */
    public DataOutputWrapperStream(final DataOutput dataOutput) {
        this.dataOutput = Objects.requireNonNull(dataOutput);
    }

    @Override
    public void write(final int b) throws IOException {
        dataOutput.write(b);
    }

    @Override
    public void write(final byte b[]) throws IOException {
        dataOutput.write(b);
    }

    @Override
    public void write(final byte b[], final int off, final int len)
            throws IOException {
        dataOutput.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        if (dataOutput instanceof Flushable) {
            ((Flushable) dataOutput).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (dataOutput instanceof Closeable) {
            ((Closeable) dataOutput).close();
        }
    }
}
