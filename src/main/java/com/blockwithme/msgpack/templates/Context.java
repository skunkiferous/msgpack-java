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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.blockwithme.msgpack.schema.Schema;
import com.blockwithme.msgpack.schema.SchemaManager;

/**
 * Represents the context information for this serialization.
 *
 * It is used mostly, to allow third-party extensions, that would give context
 * information required by custom templates.
 *
 * @author monster
 */
public class Context {

    /** Is this a required field? (Currently unused) */
    public boolean required;

    /** The format version for the current (de)serialisation. */
    public int format = Schema.FORMAT;

    /** The schema version for the current (de)serialisation. */
    public int schemaID = -1;

    /** The schema manager */
    private final SchemaManager schemaManager;

    /** The schema */
    private Schema schema;

    /**
     * All the templates, each at the right position.
     *
     * @param idToTemplate
     */
    protected Context(final SchemaManager theSchemaManager) {
        // Validate input
        Objects.requireNonNull(theSchemaManager);
        schemaManager = theSchemaManager;
    }

    /** The schema */
    public final Schema getSchema() {
        if (schema == null) {
            schema = schemaManager.getSchema(format, schemaID);
        }
        return schema;
    }

    /** Returns the Template<?> for an ID, or fails. */
    public Template<?> getTemplate(final int id) {
        try {
            final Template<?> result = getSchema().idToTemplate[id];
            if (result != null) {
                return result;
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
            // NOP
        }
        throw new IllegalArgumentException("Template not found for id: " + id);
    }

    /** Un-array a class. */
    private Class<?> unArray(final Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Object array is a special case
        Class<?> c = cls;
        Class<?> cc;
        while (c.isArray() && !(cc = c.getComponentType()).isPrimitive()) {
            c = cc;
        }
        return c;
    }

    /** Returns the ID for a Class, or null if not found. */
    @SuppressWarnings("unchecked")
    public <E> Template<E> findTemplate(final Class<E> cls) {
        final Template<E> result = (Template<E>) getSchema().classToTemplate
                .get(unArray(cls));
        if (result == null) {
            for (final Template<?> f : getSchema().fallbackTemplates) {
                if (f.getType().isAssignableFrom(cls)) {
                    return (Template<E>) f;
                }
            }
        }
        return result;
    }

    /** Returns the ID for a Class. */
    public <E> Template<E> getTemplate(final Class<E> cls) {
        final Template<E> t = findTemplate(cls);
        if (t == null) {
            throw new IllegalArgumentException("Template not found: " + cls);
        }
        return t;
    }

    /** Returns the ID for a Class, or null if not found. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <E> Template<E>[] findTemplates(final Class<E> cls) {
        final Class<?> c = unArray(cls);
        final List<Template> list = new ArrayList<Template>();
        final Template<?>[] idToTemplate = getSchema().idToTemplate;
        for (int i = 0; i < idToTemplate.length; i++) {
            final Template<?> template = idToTemplate[i];
            if (template != null) {
                if (template.getType() == c) {
                    list.add(template);
                }
            }
        }
        return list.toArray(new Template[list.size()]);
    }
}
