//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package com.blockwithme.msgpack.impl.accept;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class ByteArrayAccept extends Accept {
    public byte[] value;

    @Override
    public void acceptRaw(final byte[] raw) {
        this.value = raw;
    }

    @Override
    public void acceptEmptyRaw() {
        this.value = new byte[0];
    }

    @Override
    public void refer(final ByteBuffer bb, final boolean gift)
            throws IOException {
        // TODO gift
        this.value = new byte[bb.remaining()];
        bb.get(value);
    }
}
