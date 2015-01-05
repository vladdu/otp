
import org.junit.Assert;
import org.junit.Test;

import com.ericsson.otp.erlang.OtpErlang;
import com.ericsson.otp.erlang.OtpErlangObject;

public class OtpFormatTest {

    @Test
    public void testFormatParser_simple_1() throws Exception {
        final OtpErlangObject value = OtpErlang.format("~a", "hej");
        final OtpErlangObject expected = OtpErlang.parse("hej");
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFormatParser_simple_2() throws Exception {
        final OtpErlangObject value = OtpErlang.format("~s", "hej");
        final OtpErlangObject expected = OtpErlang.parse("\"hej\"");
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFormatParser_simple_3() throws Exception {
        final OtpErlangObject value = OtpErlang.format("~x");
        final OtpErlangObject expected = OtpErlang.parse("~x");
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFormatParser_list() throws Exception {
        final OtpErlangObject value = OtpErlang.format("[~a,2,~a]", "hej",
                "brr");
        final OtpErlangObject expected = OtpErlang.parse("[hej,2,brr]");
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFormatParser_tuple() throws Exception {
        final OtpErlangObject value = OtpErlang.format("{~a,2,~a}", "hej",
                "brr");
        final OtpErlangObject expected = OtpErlang.parse("{hej,2,brr}");
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFormatParser_full() throws Exception {
        final OtpErlangObject value = OtpErlang.format("[~a,{2,~a},5]", "hej",
                "brr");
        final OtpErlangObject expected = OtpErlang.parse("[hej,{2,brr},5]");
        Assert.assertEquals(expected, value);
    }

}
