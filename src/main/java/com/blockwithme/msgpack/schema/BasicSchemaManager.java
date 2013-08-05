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
import java.util.List;
import java.util.Objects;

import com.blockwithme.msgpack.templates.Template;

/**
 * BasicSchemaManager assumes templates never get change ID.
 *
 * @author monster
 */
public abstract class BasicSchemaManager extends SchemaManagerBase {

    /** The user templates */
    private final Template<?>[] userTemplates;

    /** Creates a BasicSchemaManager */
    protected BasicSchemaManager(final Template<?>[] theUserTemplates) {
        userTemplates = Objects.requireNonNull(theUserTemplates);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.schema.SchemaManagerBase#createSchema(int, int)
     */
    @Override
    protected Schema createSchema(final int format, final int schemaID) {
        final List<Template<?>> list = new ArrayList<Template<?>>(
                userTemplates.length);
        int lastNotNull = 0;
        for (int i = 0; i < userTemplates.length; i++) {
            if (userTemplates[i].isSchemaSupported(schemaID)) {
                list.add(userTemplates[i]);
                lastNotNull = i;
            } else {
                list.add(null);
            }
        }
        while (list.size() > lastNotNull + 1) {
            list.remove(list.size() - 1);
        }
        return createSchema(format, schemaID,
                list.toArray(new Template<?>[list.size()]),
                getBasicTemplateCount(schemaID));
    }

    /** Returns the basicTemplates count, for the given schema. */
    protected abstract int getBasicTemplateCount(final int schemaID);
}
