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
package com.blockwithme.msgpack.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.Template;
import com.blockwithme.msgpack.templates._Template;

/**
 * Base implementation of SchemaManager.
 *
 * @author monster
 */
public abstract class SchemaManagerBase implements SchemaManager {

    /** Map ID to schema. */
    private final Map<Integer, Schema> schemas = new HashMap<Integer, Schema>();

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.schema.SchemaManager#getSchema(int)
     */
    @Override
    public final Schema getSchema(final int format, final int schemaID) {
        if (format < 0) {
            throw new IllegalArgumentException("format: " + format);
        }
        if (schemaID < 1) {
            throw new IllegalArgumentException("schemaID: " + schemaID);
        }
        final Integer key = schemaID;
        Schema result;
        synchronized (schemas) {
            result = schemas.get(key);
            if (result == null) {
                result = createSchema(format, schemaID);
                schemas.put(key, result);
            }
        }
        return result;
    }

    /**
     * Creates and returns a new schema, given the user templates, and the
     * number of basic templates.
     *
     * @param schemaID
     * @return
     */
    protected final Schema createSchema(final int format, final int schemaID,
            final Template<?>[] userTemplates, final int basicTemplateCount) {
        final BasicTemplates basicTemplates = new BasicTemplates();
        final Map<Class<?>, Template<?>> classToTemplate = new HashMap<Class<?>, Template<?>>();
        final Template<?>[] bt = basicTemplates
                .getBasicTemplates(basicTemplateCount);
        final Template<?>[] idToTemplate = new Template<?>[bt.length
                + userTemplates.length];
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
            if (idToTemplate[i].isFallBackTemplate()) {
                fallBack.add(idToTemplate[i]);
            }
        }
        final Template<?>[] fallbackTemplates = fallBack
                .toArray(new Template<?>[fallBack.size()]);
        final Schema result = new Schema(format, schemaID, basicTemplates,
                idToTemplate, classToTemplate, fallbackTemplates);
        for (int i = 0; i < idToTemplate.length; i++) {
            ((_Template) idToTemplate[i]).resolve(result);
        }
        return result;
    }

    /**
     * Creates and returns a new schema.
     *
     * @param format
     * @param schemaID
     * @return
     */
    protected abstract Schema createSchema(final int format, final int schemaID);

}
