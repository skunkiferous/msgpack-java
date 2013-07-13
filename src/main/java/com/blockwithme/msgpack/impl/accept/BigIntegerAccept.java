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

import java.math.BigInteger;

/**
 * Part of the original low-level Message-Pack Java implementation.
 *
 * @author monster
 */
public final class BigIntegerAccept extends Accept {
    public BigInteger value;

    @Override
    public void acceptInteger(final byte v) {
        this.value = BigInteger.valueOf(v);
    }

    @Override
    public void acceptInteger(final short v) {
        this.value = BigInteger.valueOf(v);
    }

    @Override
    public void acceptInteger(final int v) {
        this.value = BigInteger.valueOf(v);
    }

    @Override
    public void acceptInteger(final long v) {
        this.value = BigInteger.valueOf(v);
    }

    @Override
    public void acceptUnsignedInteger(final byte v) {
        this.value = BigInteger.valueOf(v & 0xff);
    }

    @Override
    public void acceptUnsignedInteger(final short v) {
        this.value = BigInteger.valueOf(v & 0xffff);
    }

    @Override
    public void acceptUnsignedInteger(final int v) {
        if (v < 0) {
            this.value = BigInteger.valueOf((v & 0x7fffffff) + 0x80000000L);
        } else {
            this.value = BigInteger.valueOf(v);
        }
    }

    @Override
    public void acceptUnsignedInteger(final long v) {
        if (v < 0L) {
            this.value = BigInteger.valueOf(v + Long.MAX_VALUE + 1L).setBit(63);
        } else {
            this.value = BigInteger.valueOf(v);
        }
    }
}
