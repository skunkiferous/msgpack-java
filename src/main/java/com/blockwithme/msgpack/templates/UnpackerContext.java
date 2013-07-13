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

import com.blockwithme.msgpack.ObjectUnpacker;
import com.blockwithme.msgpack.Unpacker;

/**
 * Represents the context information for this serialization, while unpacking.
 *
 * @author monster
 */
public class UnpackerContext extends Context {
    /**
     * @param idToTemplate
     */
    public UnpackerContext(final Template<?>[] idToTemplate) {
        super(idToTemplate);
    }

    /** Tracks the previously returned objects. */
    public final ArrayList<Object> previous = new ArrayList<Object>(256);

    /** The Unpacker. */
    public Unpacker unpacker;

    /** The Object Unpacker. */
    public ObjectUnpacker objectUnpacker;
}
