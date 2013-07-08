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
import java.util.Objects;

/**
 * Object template, for anything beyond primitive types.
 *
 * @author monster
 */
public abstract class AbstractTemplate<T> implements Template<T> {
    /** The template ID. */
    protected final int id;

    /** The type that is supported. */
    protected final Class<T> type;

    /** Constructor. */
    protected AbstractTemplate(final int id, final Class<T> type) {
        this.id = id;
        this.type = Objects.requireNonNull(type);
    }

    /** Returns the template ID. */
    @Override
    public final int getID() {
        return id;
    }

    /** Returns the type that is supported. */
    @Override
    public final Class<T> getType() {
        return type;
    }

    /**
     * Writes a value. The value cannot be null. The Template must check the
     * type matching, and write first the type ID.
     */
    @Override
    public final void checkAndWrite(final PackerContext context, final T value)
            throws IOException {
        if (value.getClass() != type) {
            if ((this != BasicTemplates.OBJECT_ARRAY)
                    || !value.getClass().isArray()
                    || value.getClass().getComponentType().isPrimitive()) {
                if ((this != BasicTemplates.ENUM) || !value.getClass().isEnum()) {
                    throw new IOException("Expected a " + type + " but got a "
                            + value.getClass());
                }
            }
        }
        context.packer.write(id);
        write2(context, value);
    }

    /**
     * Writes a value. The value cannot be null and will be of the right type.
     */
    @Override
    public final void write(final PackerContext context, final T value)
            throws IOException {
        context.packer.write(id);
        write2(context, value);
    }

    /**
     * Writes a value. The value cannot be null and will be of the right type.
     * No ID is written.
     */
    @Override
    public final void writeNoID(final PackerContext context, final T value)
            throws IOException {
        write2(context, value);
    }

    /** Writes a non-null value (assumes some failure on null). */
    protected abstract void write2(final PackerContext context, final T value)
            throws IOException;
}
