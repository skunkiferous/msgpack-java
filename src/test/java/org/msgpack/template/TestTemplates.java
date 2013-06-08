package org.msgpack.template;

import static org.junit.Assert.assertEquals;
import static org.msgpack.template.Templates.TBigDecimal;
import static org.msgpack.template.Templates.TBigInteger;
import static org.msgpack.template.Templates.TBoolean;
import static org.msgpack.template.Templates.TByte;
import static org.msgpack.template.Templates.TByteArray;
import static org.msgpack.template.Templates.TByteBuffer;
import static org.msgpack.template.Templates.TCharacter;
import static org.msgpack.template.Templates.TDate;
import static org.msgpack.template.Templates.TDouble;
import static org.msgpack.template.Templates.TFloat;
import static org.msgpack.template.Templates.TInteger;
import static org.msgpack.template.Templates.TLong;
import static org.msgpack.template.Templates.TShort;
import static org.msgpack.template.Templates.TString;
import static org.msgpack.template.Templates.tCollection;
import static org.msgpack.template.Templates.tList;
import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.tOrdinalEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;

public class TestTemplates {
    public static enum MyEnum {
        A, B, C;
    }

    @SuppressWarnings("unused")
    @Test
    public void testGenericsTypesCompliable() throws IOException {
        Template<Byte> tbyte = TByte;
        Template<Short> tshort = TShort;
        Template<Integer> tinteger = TInteger;
        Template<Long> tlong = TLong;
        Template<Character> tcharacter = TCharacter;
        Template<BigInteger> tbiginteger = TBigInteger;
        Template<BigDecimal> tbigdecimail = TBigDecimal;
        Template<Float> tfloat = TFloat;
        Template<Double> tdouble = TDouble;
        Template<Boolean> tboolean = TBoolean;
        Template<String> tstring = TString;
        Template<byte[]> tbytearray = TByteArray;
        Template<ByteBuffer> tbytebuffer = TByteBuffer;
        Template<Date> tdate = TDate;

        Template<List<String>> tlist = tList(TString);
        Template<Map<String, Integer>> tmap = tMap(TString, TInteger);
        Template<Collection<Long>> tcollection = tCollection(TLong);
        Template<MyEnum> tordinalenum = tOrdinalEnum(MyEnum.class);
    }

    @Test
    public void testList() throws IOException {
        MessagePack msgpack = new MessagePack();

        BufferPacker pk = msgpack.createBufferPacker();

        Template<List<String>> t = tList(TString);
        List<String> list1 = new ArrayList<String>();
        list1.add("a");
        list1.add("b");
        t.write(pk, list1);

        byte[] raw = pk.toByteArray();
        Unpacker u = msgpack.createBufferUnpacker(raw);
        List<String> list2 = t.read(u, new ArrayList<String>());

        assertEquals(list1, list2);
    }
}
