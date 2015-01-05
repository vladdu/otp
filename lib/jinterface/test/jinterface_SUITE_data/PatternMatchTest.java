/*******************************************************************************
 * Copyright (c) 2004 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/

import org.junit.Assert;
import org.junit.Test;

import com.ericsson.otp.erlang.OtpBindings;
import com.ericsson.otp.erlang.OtpErlang;
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpParser;
import com.ericsson.otp.erlang.OtpParserException;
import com.ericsson.otp.erlang.OtpPatternVariable;

public class PatternMatchTest {

    private final OtpParser termParser = OtpErlang.getTermParser();

    @Test
    public void testMatch_novar() throws Exception {
        final OtpErlangObject t1 = OtpErlang.parse("[a, {b}]");
        final OtpBindings r = OtpErlang.match("[a, {b}]", t1);
        Assert.assertNotNull(r);
    }

    @Test
    public void testMatch() throws Exception {
        final OtpBindings r = OtpErlang.match("[W, V]", "[a, b]");
        Assert.assertEquals(r.get("W"), new OtpErlangAtom("a"));
        Assert.assertEquals(r.get("V"), new OtpErlangAtom("b"));
    }

    @Test
    public void testMatch_0() throws Exception {
        final OtpBindings b = new OtpBindings();
        b.put("W", new OtpErlangAtom("a"));
        final OtpBindings r = OtpErlang.match("[W, V]", "[a, b]", b);
        Assert.assertNotNull(r);
        Assert.assertEquals(r.get("V"), new OtpErlangAtom("b"));
    }

    @Test
    public void testMatch_1() throws Exception {
        final OtpBindings r = OtpErlang.match("[W, V]", "[\"a\", {[1, 2]}]");
        Assert.assertEquals(r.get("W"), new OtpErlangString("a"));
        Assert.assertEquals(r.get("V"), OtpErlang.parse("{[1, 2]}"));
    }

    @Test
    public void testMatch_same() throws Exception {
        final OtpBindings r = OtpErlang.match("[W, {V}]", "[a, {a}]");
        Assert.assertEquals(r.get("W"), new OtpErlangAtom("a"));
    }

    @Test
    public void testMatch_any() throws Exception {
        final OtpBindings r = OtpErlang.match("[_, {_}]", "[a, {b}]");
        Assert.assertNotNull(r);
    }

    @Test
    public void testMatch_same_fail() throws Exception {
        final OtpBindings r = OtpErlang.match("[W, {W}]", "[a, {b}]");
        Assert.assertNull(r);
    }

    @Test
    public void testMatch_ellipsis_1() throws Exception {
        final OtpErlangObject r = termParser.parse("[x|_]");
        Assert.assertNotNull(r);
    }

    @Test
    public void testMatch_ellipsis_2() throws Exception {
        final OtpBindings r = OtpErlang.match("[X | T]", "[x,y,z]");
        Assert.assertNotNull(r);
        Assert.assertEquals(new OtpErlangAtom("x"), r.get("X"));
        Assert.assertEquals(termParser.parse("[y,z]"), r.get("T"));
    }

    @Test()
    public void testMatch_ellipsis_4() throws Exception {
        final OtpBindings r = OtpErlang.match("[X | y]", "[x,y,z]");
        Assert.assertNull(r);
    }

    @Test(expected = OtpParserException.class)
    public void testMatch_ellipsis_5() throws Exception {
        OtpErlang.match("[X | Y, Z]", "[x,y,z]");
    }

    // new match tests

    @Test
    public void testVarMatch() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = new OtpPatternVariable("A");
        final OtpErlangObject t = new OtpErlangAtom("foo");
        v.matchTerm(t, b);
        Assert.assertEquals(b.get("A"), t);
    }

    @Test
    public void testObjectMatch() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = new OtpErlangAtom("foo");
        final OtpErlangObject t = new OtpErlangAtom("foo");
        v.matchTerm(t, b);
    }

    @Test
    public void testObjectNoMatch() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = new OtpErlangAtom("bar");
        final OtpErlangObject t = new OtpErlangAtom("foo");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_1() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a,1]");
        final OtpErlangObject t = OtpErlang.format("[a,1]");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_2() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a,1]");
        final OtpErlangObject t = OtpErlang.format("[a]");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_3() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a]");
        final OtpErlangObject t = OtpErlang.format("[a,1]");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_4() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a|1]");
        final OtpErlangObject t = OtpErlang.format("[a|1]");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_5() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a|1]");
        final OtpErlangObject t = OtpErlang.format("[a]");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_6() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a]");
        final OtpErlangObject t = OtpErlang.format("[a|1]");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testListMatch_7() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a|T]");
        final OtpErlangObject t = OtpErlang.format("[a]");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("T"), new OtpErlangList());
    }

    @Test
    public void testListMatch_8() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a|T]");
        final OtpErlangObject t = OtpErlang.format("[a,1]");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("T"), new OtpErlangList(new OtpErlangLong(1)));
    }

    @Test
    public void testListMatch_9() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("[a|T]");
        final OtpErlangObject t = OtpErlang.format("[a,1|3]");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("T"), new OtpErlangList(
                new OtpErlangObject[] { new OtpErlangLong(1) },
                new OtpErlangLong(3)));
    }

    @Test
    public void testTupleMatch_0() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,3}");
        final OtpErlangObject t = OtpErlang.format("{a,3}");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testTupleMatch_1() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,3}");
        final OtpErlangObject t = OtpErlang.format("{a,2}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testTupleMatch_2() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,3}");
        final OtpErlangObject t = OtpErlang.format("{a,3,2}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testTupleMatch_3() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,3,2}");
        final OtpErlangObject t = OtpErlang.format("{a,3}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testTupleMatch_4() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,A}");
        final OtpErlangObject t = OtpErlang.format("{a,3}");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("A"), new OtpErlangLong(3));
    }

    @Test
    public void testTupleMatch_5() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{a,A}");
        final OtpErlangObject t = OtpErlang.format("{b,3}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testTupleMatch_6() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{A,A}");
        final OtpErlangObject t = OtpErlang.format("{b,b}");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("A"), new OtpErlangAtom("b"));
    }

    @Test
    public void testTupleMatch_7() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("{A,A}");
        final OtpErlangObject t = OtpErlang.format("{b,c}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_0() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{a=>1,b=>3}");
        final OtpErlangObject t = OtpErlang.format("#{a=>1,b=>3}");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_1() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{a=>1,b=>3}");
        final OtpErlangObject t = OtpErlang.format("#{b=>3,a=>1}");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_2() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{a=>1,b=>3}");
        final OtpErlangObject t = OtpErlang.format("#{a=>1,b=>4}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_3() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{a=>1,b=>3}");
        final OtpErlangObject t = OtpErlang.format("#{a=>1,c=>3}");
        Assert.assertFalse(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_4() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{a=>1}");
        final OtpErlangObject t = OtpErlang.format("#{a=>1,c=>3}");
        Assert.assertTrue(v.matchTerm(t, b));
    }

    @Test
    public void testMapMatch_5() throws Exception {
        final OtpBindings b = new OtpBindings();
        final OtpErlangObject v = OtpErlang.format("#{c=>A}");
        final OtpErlangObject t = OtpErlang.format("#{a=>1,c=>3}");
        Assert.assertTrue(v.matchTerm(t, b));
        Assert.assertEquals(b.get("A"), new OtpErlangLong(3));
    }

}
