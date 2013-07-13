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
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import com.blockwithme.msgpack.MessageTypeException;

public final class StringAccept extends Accept {
    public String value;
    private final CharsetDecoder decoder;

    public StringAccept() {
        this.decoder = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    @Override
    public void acceptRaw(final byte[] raw) {
        try {
            this.value = decoder.decode(ByteBuffer.wrap(raw)).toString();
        } catch (final CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }

    @Override
    public void acceptEmptyRaw() {
        this.value = "";
    }

    @Override
    public void refer(final ByteBuffer bb, final boolean gift)
            throws IOException {
        try {
            this.value = decoder.decode(bb).toString();
        } catch (final CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }
}
