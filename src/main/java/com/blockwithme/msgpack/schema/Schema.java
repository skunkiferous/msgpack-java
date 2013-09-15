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

import java.util.Map;

import com.blockwithme.msgpack.templates.BasicTemplates;
import com.blockwithme.msgpack.templates.Template;

/**
 * Represents a serialisation schema.
 *
 * TODO toString ...
 *
 * @author monster
 */
public class Schema {

    /** The current format version. */
    public static final int FORMAT = 0;

    /** The format version for the current (de)serialisation. */
    public final int format;

    /** The schema version for the current (de)serialisation. */
    public final int schema;

    /** The basic templates. */
    public final BasicTemplates basicTemplates;

    /** Maps IDs to Class templates */
    public final Template<?>[] idToTemplate;

    /** Maps Class to Template. */
    public final Map<Class<?>, Template<?>> classToTemplate;

    /** The "fallback" (catch-all) templates */
    public final Template<?>[] fallbackTemplates;

    /** Constructor */
    public Schema(final int theFormat, final int theSchema,
            final BasicTemplates theBasicTemplates,
            final Template<?>[] theIdToTemplate,
            final Map<Class<?>, Template<?>> theClassToTemplate,
            final Template<?>[] theFallbackTemplates) {
        format = theFormat;
        schema = theSchema;
        basicTemplates = theBasicTemplates;
        idToTemplate = theIdToTemplate;
        classToTemplate = theClassToTemplate;
        fallbackTemplates = theFallbackTemplates;
    }

}
