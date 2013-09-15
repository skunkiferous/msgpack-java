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
package com.blockwithme.util;

import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * ObjectTracker keep track of object, to make sure that multiple occurrences
 * are detected. Depending on the value of the isImmutable flag, we will use
 * either Object equality, if true, or Object identity, if false.
 *
 * TODO I expect the performance is bad. We want something faster.
 *
 * @author monster
 */
public class ObjectTracker {
    /** Stores the non-String objects. */
    private final IdentityHashMap<Object, Integer> normal = new IdentityHashMap<Object, Integer>();

    /** Stores the String objects. */
    private final HashMap<Object, Integer> immutable = new HashMap<Object, Integer>();

    /** Tracks an object. Returns -1 if new or null, otherwise the insertion position. */
    public int track(final Object o, final boolean isImmutable) {
        if (o == null) {
            return -1;
        }
        if (isImmutable) {
            final Integer pos = immutable.get(o);
            if (pos == null) {
                immutable.put(o, position());
                return -1;
            }
            return pos;
        }
        final Integer pos = normal.get(o);
        if (pos == null) {
            normal.put(o, position());
            return -1;
        }
        return pos;
    }

    /** Clears the tracker. */
    public void clear() {
        normal.clear();
        immutable.clear();
    }

    /** Return the position of the last new object. Positions start at 1. */
    public int position() {
        return normal.size() + immutable.size();
    }
}
