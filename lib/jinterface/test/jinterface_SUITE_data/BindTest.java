
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.otp.erlang.OtpBindings;
import com.ericsson.otp.erlang.OtpErlang;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpMatchException;

public class BindTest {

    private final OtpBindings binds = new OtpBindings();

    @Before
    public void before() throws Exception {
        binds.clear();
        binds.put("A", OtpErlang.format("a"));
        binds.put("B", OtpErlang.format("1"));
        binds.put("C", OtpErlang.format("[]"));
    }

    @Test
    public void bindNothing() throws Exception {
        final OtpErlangObject t = OtpErlang.format("[{foo}]");
        final OtpErlangObject actual = t.bind(binds);
        final OtpErlangObject expected = t;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bindVar() throws Exception {
        final OtpErlangObject t = OtpErlang.format("A");
        final OtpErlangObject actual = t.bind(binds);
        final OtpErlangObject expected = OtpErlang.format("a");
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = OtpMatchException.class)
    public void bindVarException() throws Exception {
        final OtpErlangObject t = OtpErlang.format("X");
        final OtpErlangObject actual = t.bind(binds);
        final OtpErlangObject expected = OtpErlang.format("a");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bindVarPartial() throws Exception {
        final OtpErlangObject t = OtpErlang.format("X");
        final OtpErlangObject actual = t.bindPartial(binds);
        final OtpErlangObject expected = t;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bindMany() throws Exception {
        final OtpErlangObject t = OtpErlang.format("[{A},B,#{d=>A}]");
        final OtpErlangObject actual = t.bind(binds);
        final OtpErlangObject expected = OtpErlang.format("[{a},1,#{d=>a}]");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bindPartialMany() throws Exception {
        final OtpErlangObject t = OtpErlang.format("[{A},X,#{d=>A}]");
        final OtpErlangObject actual = t.bindPartial(binds);
        final OtpErlangObject expected = OtpErlang.format("[{a},X,#{d=>a}]");
        Assert.assertEquals(expected, actual);
    }

}
