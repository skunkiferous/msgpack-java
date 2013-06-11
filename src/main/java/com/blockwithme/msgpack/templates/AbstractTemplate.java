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
package com.blockwithme.msgpack.templates;

import java.io.IOException;

import com.blockwithme.msgpack.Packer;
import com.blockwithme.msgpack.Unpacker;

/**
 * Object template, for anything beyond primitive types.
 *
 * @author monster
 */
public abstract class AbstractTemplate<T> implements Template<T> {

    /** Reads a value. */
    @Override
    public final T read(final Unpacker u, final Context context)
            throws IOException {
        if (u.trySkipNil()) {
            if (!context.required) {
                return null;
            }
            throw new IOException("Attempted to read null");
        }
        return read(u, context);
    }

    /** Writes a value. */
    @Override
    public final void write(final Packer pk, final T value,
            final Context context) throws IOException {
        if (value == null) {
            if (context.required) {
                throw new IOException("Attempted to write null");
            }
            pk.writeNil();
            return;
        }
        write(pk, value, context);
    }

    /** Reads a non-null value. */
    protected abstract T _read(final Unpacker u, final Context context)
            throws IOException;

    /** Writes a non-null value. */
    protected abstract void _write(final Packer pk, final T value,
            final Context context) throws IOException;
}
