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

/**
 * Default non-OSGi implementation of ClassNameConverter.
 *
 * @author monster
 */
public class DefaultClassNameConverter implements ClassNameConverter {

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.ClassNameConverter#getName(java.lang.Class)
     */
    @Override
    public String getName(final Class<?> cls) {
        return cls.getName();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.msgpack.templates.ClassNameConverter#getClass(java.lang.String)
     */
    @Override
    public Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Class Not Found: " + name);
        }
    }

}
