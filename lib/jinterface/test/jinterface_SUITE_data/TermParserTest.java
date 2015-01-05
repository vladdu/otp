
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.ericsson.otp.erlang.OtpErlang;
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangMap;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpFormatPlaceholder;
import com.ericsson.otp.erlang.OtpOutputStream;
import com.ericsson.otp.erlang.OtpParser;
import com.ericsson.otp.erlang.OtpParserException;
import com.ericsson.otp.erlang.OtpPatternVariable;

public class TermParserTest {

    private final OtpParser termParser = OtpErlang.getTermParser();

    @Test
    public void int_1() throws OtpParserException {
        final OtpErlangLong r = (OtpErlangLong) termParser.parse("34");
        Assert.assertEquals(r.longValue(), 34);
    }

    @Test
    public void int_2() throws OtpParserException {
        final OtpErlangLong r = (OtpErlangLong) termParser.parse("-34");
        Assert.assertEquals(r.longValue(), -34);
    }

    @Test
    public void atom_1() throws OtpParserException {
        final OtpErlangAtom r = (OtpErlangAtom) termParser.parse("hello");
        Assert.assertEquals(r.atomValue(), "hello");
    }

    @Test
    public void atom_2() throws OtpParserException {
        final OtpErlangAtom r = (OtpErlangAtom) termParser.parse("hello   ");
        Assert.assertEquals(r.atomValue(), "hello");
    }

    @Test
    public void atom_3() throws OtpParserException {
        final OtpErlangAtom r = (OtpErlangAtom) termParser.parse("   hello");
        Assert.assertEquals(r.atomValue(), "hello");
    }

    @Test
    public void number_1() throws OtpParserException {
        final OtpErlangLong r = (OtpErlangLong) termParser.parse("321");
        Assert.assertEquals(r.longValue(), 321);
    }

    @Test
    public void var_1() throws OtpParserException {
        final OtpPatternVariable r = (OtpPatternVariable) termParser
                .parse("Hello");
        Assert.assertEquals(r.getName(), "Hello");
    }

    @Test
    public void var_2() throws OtpParserException {
        final OtpPatternVariable r = (OtpPatternVariable) termParser.parse("_");
        Assert.assertEquals(r.getName(), "_");
    }

    @Test
    public void string_1() throws OtpParserException {
        final OtpErlangString r = (OtpErlangString) termParser
                .parse("\"Hello\"");
        Assert.assertEquals(r.stringValue(), "Hello");
    }

    @Test
    public void string_2() throws OtpParserException {
        final OtpErlangString r = (OtpErlangString) termParser
                .parse("\"Hello world!\"");
        Assert.assertEquals(r.stringValue(), "Hello world!");
    }

    @Test
    public void placeholder_1() throws OtpParserException {
        final OtpFormatPlaceholder r = (OtpFormatPlaceholder) termParser
                .parse("~hello");
        Assert.assertEquals(r.getName(), "hello");
    }

    @Test
    public void list_1() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[]");
        Assert.assertEquals("[]", r.toString());
    }

    @Test
    public void list_2() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[a,2,b,4]");
        Assert.assertEquals("[a,2,b,4]", r.toString());
    }

    @Test
    public void list_3() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[a,2,b|4]");
        Assert.assertEquals("[a,2,b|4]", r.toString());
    }

    @Test
    public void list_4() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser
                .parse("[ax , [ 2  , b ], 4 ,5]");
        Assert.assertEquals("[ax,[2,b],4,5]", r.toString());
    }

    @Test
    public void sublist_1() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[a,2,b,4]");
        final OtpErlangList s = (OtpErlangList) termParser.parse("[2,b,4]");
        final OtpErlangObject ss = r.getTail();
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_2() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3|4]");
        final OtpErlangList s = (OtpErlangList) termParser.parse("[2,3|4]");
        final OtpErlangObject ss = r.getTail();
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_4() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3,4]");
        final OtpErlangList s = (OtpErlangList) termParser.parse("[3,4]");
        final OtpErlangObject ss = r.getNthTail(2);
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_3() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3|4]");
        final OtpErlangList s = (OtpErlangList) termParser.parse("[3|4]");
        final OtpErlangObject ss = r.getNthTail(2);
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_4a() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3,4]");
        final OtpErlangObject ss = r.getNthTail(0);
        Assert.assertEquals(r, ss);
    }

    @Test
    public void sublist_4b() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3,4]");
        final OtpErlangObject ss = r.getNthTail(4);
        Assert.assertEquals(new OtpErlangList(), ss);
    }

    @Test
    public void sublist_4c() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3,4]");
        final OtpErlangObject ss = r.getNthTail(5);
        Assert.assertEquals(null, ss);
    }

    @Test
    public void sublist_4d() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3|4]");
        final OtpErlangObject s = termParser.parse("[3|4]");
        final OtpErlangObject ss = r.getNthTail(2);
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_4e() throws OtpParserException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3|4]");
        final OtpErlangObject s = termParser.parse("4");
        final OtpErlangObject ss = r.getNthTail(3);
        Assert.assertEquals(s, ss);
    }

    @Test
    public void sublist_5() throws OtpParserException, IOException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3,4]");
        final OtpErlangObject ss = r.getNthTail(2);
        final OtpOutputStream out = new OtpOutputStream();
        ss.encode(out);
        final byte[] contents1 = out.toByteArray();
        out.reset();
        final OtpErlangList s = (OtpErlangList) termParser.parse("[3,4]");
        s.encode(out);
        final byte[] contents2 = out.toByteArray();
        out.close();

        Assert.assertEquals(contents2.length, contents1.length);
        for (int i = 0; i < contents1.length; i++) {
            Assert.assertEquals(contents2[i], contents1[i]);
        }
    }

    @Test
    public void sublist_6() throws OtpParserException, IOException {
        final OtpErlangList r = (OtpErlangList) termParser.parse("[1,2,3|4]");
        final OtpErlangObject ss = r.getNthTail(2);
        final OtpOutputStream out = new OtpOutputStream();
        ss.encode(out);
        final byte[] contents1 = out.toByteArray();
        out.reset();
        final OtpErlangList s = (OtpErlangList) termParser.parse("[3|4]");
        s.encode(out);
        final byte[] contents2 = out.toByteArray();
        out.close();

        Assert.assertEquals(contents2.length, contents1.length);
        for (int i = 0; i < contents1.length; i++) {
            Assert.assertEquals(contents2[i], contents1[i]);
        }
    }

    @Test
    public void map_1() throws OtpParserException {
        final OtpErlangMap r = (OtpErlangMap) termParser.parse("#{}");
        Assert.assertEquals("#{}", r.toString());
    }

    @Test
    public void map_2() throws OtpParserException {
        @SuppressWarnings("unused")
        final OtpErlangMap r = (OtpErlangMap) termParser
                .parse("#{a=>2,\"b\"=>[4]}");
    }

    @Test(expected = OtpParserException.class)
    public void map_3() throws OtpParserException {
        @SuppressWarnings("unused")
        final OtpErlangMap r = (OtpErlangMap) termParser.parse("#{a=>2,b}");
    }

}
