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
 * A Packable object knows it's own template.
 *
 * @author monster
 */
public interface Packable<P extends Packable<P>> {
    /** Returns own template; null if unknown. */
    Template<P> getTemplate();

    /** Sets the template to use, so next call to getTemplate() can return something. */
    void setTemplate(final Template<P> tempalte);
}
