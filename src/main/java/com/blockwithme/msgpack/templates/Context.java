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
import java.util.HashMap;
import java.util.List;
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

    /** The current format version. */
    public static final int FORMAT = 0;

    /** The format version for the current (de)serialisation. */
    public int format;

    /** The schema version for the current (de)serialisation. */
    public int schema;

    /** The basic templates. */
    public final BasicTemplates basicTemplates = new BasicTemplates();

    /** Maps IDs to Class templates */
    private final Template<?>[] idToTemplate;

    /** Maps Class to Template. */
    private final Map<Class<?>, Template<?>> classToTemplate = new HashMap<Class<?>, Template<?>>();

    /** The "fallback" (catch-all) templates */
    private final Template<?>[] fallbackTemplates;

    /**
     * All the templates, each at the right position.
     *
     * @param idToTemplate
     */
    protected Context(final Template<?>[] userTemplates) {
        // Validate input
        Objects.requireNonNull(userTemplates);
        final Template<?>[] bt = basicTemplates.getAllBasicTemplates();
        idToTemplate = new Template<?>[bt.length + userTemplates.length];
        System.arraycopy(bt, 0, idToTemplate, 0, bt.length);
        System.arraycopy(userTemplates, 0, idToTemplate, bt.length,
                userTemplates.length);
        for (int i = 0; i < idToTemplate.length; i++) {
            final Template<?> template = idToTemplate[i];
            if (template != null) {
                Objects.requireNonNull(template.getType(), "idToTemplate[" + i
                        + "].getType()");
                ((_Template) template).setID(i);
                if (template.isMainTemplate()
                        && classToTemplate.put(template.getType(), template) != null) {
                    throw new IllegalArgumentException(
                            "Multiple main templates for " + template.getType());
                }
            }
        }
        final List<Template<?>> fallBack = new ArrayList<Template<?>>();
        for (int i = 0; i < idToTemplate.length; i++) {
            ((_Template) idToTemplate[i]).resolve(this);
            if (idToTemplate[i].isFallBackTemplate()) {
                fallBack.add(idToTemplate[i]);
            }
        }
        fallbackTemplates = fallBack.toArray(new Template<?>[fallBack.size()]);
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
        final Template<E> result = (Template<E>) classToTemplate
                .get(unArray(cls));
        if (result == null) {
            for (final Template<?> f : fallbackTemplates) {
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

    /** Is this a required field? (Currently unused) */
    public boolean required;
}
