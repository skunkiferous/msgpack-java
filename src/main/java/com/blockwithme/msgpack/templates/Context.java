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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the context information for this serialization.
 *
 * It is used mostly, to allow third-party extensions, that would give context
 * information required by custom templates.
 *
 * @author monster
 */
public class Context {

    /** Maps IDs to Class templates */
    private final Template<?>[] idToTemplate;

    /** Maps Class to Template. */
    private final Map<Class<?>, Template<?>> classToTemplate = new HashMap<Class<?>, Template<?>>();

    /**
     * All the templates, each at the right position.
     *
     * @param idToTemplate
     */
    public Context(final Template<?>[] idToTemplate) {
        // Validate input
        this.idToTemplate = Objects.requireNonNull(idToTemplate);
        for (int i = 0; i < idToTemplate.length; i++) {
            final Template<?> template = idToTemplate[i];
            if (template != null) {
                Objects.requireNonNull(template.getType(), "idToTemplate[" + i
                        + "].getType()");
                if (template.getID() != i) {
                    throw new IllegalStateException(template + " at index " + i
                            + " should have ID " + i + " but has ID "
                            + template.getID());
                }
                if (classToTemplate.put(template.getType(), template) != null) {
                    throw new IllegalArgumentException(
                            "Multiple templates for " + template.getType());
                }
            }
        }
    }

    /** Returns the Template<?> for an ID, or fails. */
    public Template<?> getTemplate(final int id) {
        try {
            final Template<?> result = idToTemplate[id];
            if (result != null) {
                return result;
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
            // NOP
        }
        throw new IllegalArgumentException("Template not found for id: " + id);
    }

    /** Returns the ID for a Class, or null if not found. */
    @SuppressWarnings("unchecked")
    public <E> Template<E> findTemplate(final Class<E> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Object array is a special case
        Class<?> c = cls;
        Class<?> cc;
        while (c.isArray() && !(cc = c.getComponentType()).isPrimitive()) {
            c = cc;
        }
        return (Template<E>) classToTemplate.get(c);
    }

    /** Returns the ID for a Class. */
    public <E> Template<E> getTemplate(final Class<E> cls) {
        final Template<E> t = findTemplate(cls);
        if (t == null) {
            throw new IllegalArgumentException("Template not found: " + cls);
        }
        return t;
    }

    /** Is this a required field? (Currently unused) */
    public boolean required;
}
