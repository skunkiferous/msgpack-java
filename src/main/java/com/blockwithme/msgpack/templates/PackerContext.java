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

import com.blockwithme.msgpack.ObjectPacker;
import com.blockwithme.msgpack.Packer;
import com.blockwithme.util.ObjectTracker;

/**
 * Represents the context information for this serialization, while packing.
 *
 * It is used mostly, to allow third-party extensions, that would give context
 * information required by custom templates.
 *
 * @author monster
 */
public class PackerContext extends Context {
    /**
     * All the templates, each at the right position.
     *
     * @param idToTemplate
     */
    public PackerContext(final Template<?>[] idToTemplate) {
        super(idToTemplate);
    }

    /** The ObjectTracker */
    public final ObjectTracker tracker = new ObjectTracker();

    /** The Packer. */
    public Packer packer;

    /** The Object Packer. */
    public ObjectPacker objectPacker;
}