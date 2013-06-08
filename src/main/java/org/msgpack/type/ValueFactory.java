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
package org.msgpack.type;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public final class ValueFactory {
    public static NilValue createNilValue() {
        return NilValue.getInstance();
    }

    public static BooleanValue createBooleanValue(final boolean v) {
        if (v) {
            return TrueValueImpl.getInstance();
        } else {
            return FalseValueImpl.getInstance();
        }
    }

    public static IntegerValue createIntegerValue(final byte v) {
        return new IntValueImpl(v);
    }

    public static IntegerValue createIntegerValue(final short v) {
        return new IntValueImpl(v);
    }

    public static IntegerValue createIntegerValue(final char v) {
        return new IntValueImpl(v);
    }

    public static IntegerValue createIntegerValue(final int v) {
        return new IntValueImpl(v);
    }

    public static IntegerValue createIntegerValue(final long v) {
        return new LongValueImpl(v);
    }

    public static IntegerValue createIntegerValue(final BigInteger v) {
        return new BigIntegerValueImpl(v);
    }

    public static FloatValue createFloatValue(final float v) {
        return new FloatValueImpl(v);
    }

    public static FloatValue createFloatValue(final double v) {
        return new DoubleValueImpl(v);
    }

    public static RawValue createRawValue() {
        return ByteArrayRawValueImpl.getEmptyInstance();
    }

    public static RawValue createRawValue(final byte[] b) {
        return createRawValue(b, false);
    }

    public static RawValue createRawValue(final byte[] b, final boolean gift) {
        return new ByteArrayRawValueImpl(b, gift);
    }

    public static RawValue createRawValue(final byte[] b, final int off,
            final int len) {
        return new ByteArrayRawValueImpl(b, off, len);
    }

    public static RawValue createRawValue(final String s) {
        return new StringRawValueImpl(s);
    }

    public static RawValue createRawValue(final ByteBuffer bb) {
        final int pos = bb.position();
        try {
            final byte[] buf = new byte[bb.remaining()];
            bb.get(buf);
            return new ByteArrayRawValueImpl(buf, true);
        } finally {
            bb.position(pos);
        }
    }

    public static ArrayValue createArrayValue() {
        return ArrayValueImpl.getEmptyInstance();
    }

    public static ArrayValue createArrayValue(final Value[] array) {
        if (array.length == 0) {
            // TODO EmptyArrayValueImpl?
            return ArrayValueImpl.getEmptyInstance();
        }
        return createArrayValue(array, false);
    }

    public static ArrayValue createArrayValue(final Value[] array,
            final boolean gift) {
        if (array.length == 0) {
            // TODO EmptyArrayValueImpl?
            return ArrayValueImpl.getEmptyInstance();
        }
        return new ArrayValueImpl(array, gift);
    }

    public static MapValue createMapValue() {
        return SequentialMapValueImpl.getEmptyInstance();
    }

    public static MapValue createMapValue(final Value[] kvs) {
        if (kvs.length == 0) {
            // TODO EmptyMapValueImpl?
            return SequentialMapValueImpl.getEmptyInstance();
        }
        return createMapValue(kvs, false);
    }

    public static MapValue createMapValue(final Value[] kvs, final boolean gift) {
        if (kvs.length == 0) {
            // TODO EmptyMapValueImpl?
            return SequentialMapValueImpl.getEmptyInstance();
        }
        return new SequentialMapValueImpl(kvs, gift);
    }

    // TODO
    // public static Value get(Object obj) {
    // return new Unconverter().pack(obj).getResult();
    // }

    private ValueFactory() {
    }
}
