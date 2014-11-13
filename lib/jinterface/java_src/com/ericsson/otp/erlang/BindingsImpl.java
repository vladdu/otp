/*******************************************************************************
 * Copyright (c) 2008 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package com.ericsson.otp.erlang;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BindingsImpl implements Bindings {

    private final Map<String, OtpErlangObject> bindings;

    public BindingsImpl() {
        bindings = new HashMap<String, OtpErlangObject>();
    }

    public BindingsImpl(final Bindings binds) {
        this();
        merge(binds);
    }

    public void merge(final Bindings binds) {
        bindings.putAll(binds.getAll());
    }

    public OtpErlangObject get(final String name) {
        return bindings.get(name);
    }

    public int getInt(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangLong) {
            return ((OtpErlangLong) r).intValue();
        }
        throw new OtpErlangException("value is not an integer");
    }

    public long getLong(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangLong) {
            return ((OtpErlangLong) r).longValue();
        }
        throw new OtpErlangException("value is not an integer");
    }

    public String getAtom(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangAtom) {
            return ((OtpErlangAtom) r).atomValue();
        }
        throw new OtpErlangException("value is not an atom");
    }

    public String getQuotedAtom(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangAtom) {
            return ((OtpErlangAtom) r).toString();
        }
        throw new OtpErlangException("value is not an atom");
    }

    public String getString(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangString) {
            return ((OtpErlangString) r).stringValue();
        }
        throw new OtpErlangException("value is not a string");
    }

    public Collection<OtpErlangObject> getList(final String name)
            throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangList) {
            return OtpUtils.asCollection(((OtpErlangList) r).elements());
        }
        throw new OtpErlangException("value is not a list");
    }

    public OtpErlangObject[] getTuple(final String name)
            throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangTuple) {
            return ((OtpErlangTuple) r).elements();
        }
        throw new OtpErlangException("value is not a tuple");
    }

    public OtpErlangPid getPid(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangPid) {
            return (OtpErlangPid) r;
        }
        throw new OtpErlangException("value is not a pid");
    }

    @SuppressWarnings("unchecked")
    public <T> T getAs(final String name, final Class<T> cls)
            throws SignatureException {
        final OtpErlangObject v = get(name);
        return (T) TypeConverter.erlang2java(v, cls);
    }

    public void put(final String name, final OtpErlangObject value) {
        bindings.put(name, value);
    }

    public Map<String, OtpErlangObject> getAll() {
        return Collections.unmodifiableMap(bindings);
    }

    @Override
    public String toString() {
        return bindings.toString();
    }

    public OtpErlangBinary getBinary(final String name)
            throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangBinary) {
            return (OtpErlangBinary) r;
        }
        throw new OtpErlangException("value is not a binary");
    }

    public String getAsString(final String name) {
        final OtpErlangObject r = get(name);
        return OtpErlang.asString(r);
    }

    public double getDouble(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangDouble) {
            return ((OtpErlangDouble) r).doubleValue();
        }
        throw new OtpErlangException("value is not a double");
    }

}
