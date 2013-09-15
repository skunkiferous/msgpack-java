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

/**
 * Part of the original low-level Message-Pack Java implementation.
 *
 * @author monster
 */
public final class SkipAccept extends Accept {
    @Override
    public void acceptBoolean(final boolean v) {
    }

    @Override
    public void acceptInteger(final byte v) {
    }

    @Override
    public void acceptInteger(final short v) {
    }

    @Override
    public void acceptInteger(final int v) {
    }

    @Override
    public void acceptInteger(final long v) {
    }

    @Override
    public void acceptUnsignedInteger(final byte v) {
    }

    @Override
    public void acceptUnsignedInteger(final short v) {
    }

    @Override
    public void acceptUnsignedInteger(final int v) {
    }

    @Override
    public void acceptUnsignedInteger(final long v) {
    }

    @Override
    public void acceptRaw(final byte[] raw) {
    }

    @Override
    public void acceptEmptyRaw() {
    }

    @Override
    public void refer(final ByteBuffer bb, final boolean gift)
            throws IOException {
    }

    @Override
    public void acceptArray(final int size) {
    }

    @Override
    public void acceptMap(final int size) {
    }

    @Override
    public void acceptNil() {
    }

    @Override
    public void acceptFloat(final float v) {
    }

    @Override
    public void acceptDouble(final double v) {
    }
}
