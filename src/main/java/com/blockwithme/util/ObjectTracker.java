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
 * ObjectTracker keep track of object, using identity, to make sure that
 * multiple occurrences are detected.
 *
 * One exception are Strings, which are stored by value/equality.
 *
 * TODO I expect the performance is bad. We want something faster.
 *
 * @author monster
 */
public class ObjectTracker {
    /** Stores the non-String objects. */
    private final IdentityHashMap<Object, Integer> map = new IdentityHashMap<Object, Integer>();

    /** Stores the String objects. */
    private final HashMap<String, Integer> strings = new HashMap<String, Integer>();

    /** Tracks an object. Returns 0 if new or null, otherwise the insertion position. */
    public int track(final Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof String) {
            final String s = (String) o;
            final Integer pos = strings.get(s);
            if (pos == null) {
                strings.put(s, lastPosition());
                return 0;
            }
            return pos;
        }
        final Integer pos = map.get(o);
        if (pos == null) {
            map.put(o, lastPosition());
            return 0;
        }
        return pos;
    }

    /** Clears the tracker. */
    public void clear() {
        map.clear();
        strings.clear();
    }

    /** Return the position of the last new object. Positions start at 1. */
    public int lastPosition() {
        return map.size() + strings.size() + 1;
    }
}
