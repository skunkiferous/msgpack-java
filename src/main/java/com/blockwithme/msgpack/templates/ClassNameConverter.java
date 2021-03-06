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
 * Convert a Class to it's name and back.
 *
 * This type is required, to deal with OSGi deployments.
 *
 * @author monster
 */
public interface ClassNameConverter {
    /** Returns the Name of a Class, which cannot be null. */
    String getName(final Class<?> cls);

    /** Returns the Class for a name, which cannot be null. */
    Class<?> getClass(final String name);

    /**
     * Returns true if the type is (effectively) final.
     *
     * A class is effectively final if no other class being used extends it.
     */
    boolean isFinal(final Class<?> cls);
}
