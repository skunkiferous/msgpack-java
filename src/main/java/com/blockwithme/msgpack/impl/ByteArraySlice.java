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
package com.blockwithme.msgpack.impl;

/**
 * Small wrapper object, needed to pass a byte[] slice to a Template as a
 * single object.
 *
 * @author monster
 */
public class ByteArraySlice {

    public final byte[] o;
    public final int off;
    public final int len;

    /** Creates a byte[] slice */
    public ByteArraySlice(final byte[] o, final int off, final int len) {
        this.o = o;
        this.off = off;
        this.len = len;
    }
}
