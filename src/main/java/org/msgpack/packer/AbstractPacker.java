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
package org.msgpack.packer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.type.Value;

public abstract class AbstractPacker implements Packer {
    protected MessagePack msgpack;

    protected AbstractPacker(final MessagePack msgpack) {
        this.msgpack = msgpack;
    }

    @Override
    public Packer write(final boolean o) throws IOException {
        writeBoolean(o);
        return this;
    }

    @Override
    public Packer write(final byte o) throws IOException {
        writeByte(o);
        return this;
    }

    @Override
    public Packer write(final short o) throws IOException {
        writeShort(o);
        return this;
    }

    @Override
    public Packer write(final char o) throws IOException {
        writeChar(o);
        return this;
    }

    @Override
    public Packer write(final int o) throws IOException {
        writeInt(o);
        return this;
    }

    @Override
    public Packer write(final long o) throws IOException {
        writeLong(o);
        return this;
    }

    @Override
    public Packer write(final float o) throws IOException {
        writeFloat(o);
        return this;
    }

    @Override
    public Packer write(final double o) throws IOException {
        writeDouble(o);
        return this;
    }

    @Override
    public Packer write(final Boolean o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBoolean(o);
        }
        return this;
    }

    @Override
    public Packer write(final Byte o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByte(o);
        }
        return this;
    }

    @Override
    public Packer write(final Short o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeShort(o);
        }
        return this;
    }

    @Override
    public Packer write(final Character o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeChar(o);
        }
        return this;
    }

    @Override
    public Packer write(final Integer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeInt(o);
        }
        return this;
    }

    @Override
    public Packer write(final Long o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeLong(o);
        }
        return this;
    }

    @Override
    public Packer write(final BigInteger o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o);
        }
        return this;
    }

    @Override
    public Packer write(final Float o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeFloat(o);
        }
        return this;
    }

    @Override
    public Packer write(final Double o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeDouble(o);
        }
        return this;
    }

    @Override
    public Packer write(final byte[] o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o);
        }
        return this;
    }

    @Override
    public Packer write(final byte[] o, final int off, final int len)
            throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o, off, len);
        }
        return this;
    }

    @Override
    public Packer write(final ByteBuffer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteBuffer(o);
        }
        return this;
    }

    @Override
    public Packer write(final String o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeString(o);
        }
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Packer write(final Object o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            final Template tmpl = msgpack.lookup(o.getClass());
            tmpl.write(this, o);
        }
        return this;
    }

    @Override
    public Packer write(final Value v) throws IOException {
        if (v == null) {
            writeNil();
        } else {
            v.writeTo(this);
        }
        return this;
    }

    @Override
    public Packer writeArrayEnd() throws IOException {
        writeArrayEnd(true);
        return this;
    }

    @Override
    public Packer writeMapEnd() throws IOException {
        writeMapEnd(true);
        return this;
    }

    @Override
    public void close() throws IOException {
    }

    abstract protected void writeBoolean(final boolean v) throws IOException;

    abstract protected void writeByte(final byte v) throws IOException;

    abstract protected void writeShort(final short v) throws IOException;

    abstract protected void writeChar(final char v) throws IOException;

    abstract protected void writeInt(final int v) throws IOException;

    abstract protected void writeLong(final long v) throws IOException;

    abstract protected void writeBigInteger(final BigInteger v)
            throws IOException;

    abstract protected void writeFloat(final float v) throws IOException;

    abstract protected void writeDouble(final double v) throws IOException;

    protected void writeByteArray(final byte[] b) throws IOException {
        writeByteArray(b, 0, b.length);
    }

    abstract protected void writeByteArray(final byte[] b, final int off,
            final int len) throws IOException;

    abstract protected void writeByteBuffer(final ByteBuffer bb)
            throws IOException;

    abstract protected void writeString(final String s) throws IOException;
}
