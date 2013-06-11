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

import org.msgpack.MessageTypeException;

public abstract class Accept {
    public void acceptBoolean(final boolean v) throws IOException {
        throw new MessageTypeException("Unexpected boolean value");
    }

    public void acceptInteger(final byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptInteger(final short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptInteger(final int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptInteger(final long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptUnsignedInteger(final byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptUnsignedInteger(final short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptUnsignedInteger(final int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptUnsignedInteger(final long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    // void checkRawAcceptable() throws IOException {
    // throw new MessageTypeException("Unexpected raw value");
    // }

    public void acceptRaw(final byte[] raw) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }

    public void acceptEmptyRaw() throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }

    // void checkArrayAcceptable(int size) throws IOException {
    // throw new MessageTypeException("Unexpected array value");
    // }

    public void acceptArray(final int size) throws IOException {
        throw new MessageTypeException("Unexpected array value");
    }

    // void checkMapAcceptable(int size) throws IOException {
    // throw new MessageTypeException("Unexpected map value");
    // }

    public void acceptMap(final int size) throws IOException {
        throw new MessageTypeException("Unexpected map value");
    }

    public void acceptNil() throws IOException {
        throw new MessageTypeException("Unexpected nil value");
    }

    public void acceptFloat(final float v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    public void acceptDouble(final double v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    public void refer(final ByteBuffer bb, final boolean gift)
            throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }
}
