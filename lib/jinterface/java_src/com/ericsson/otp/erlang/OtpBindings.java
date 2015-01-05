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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class OtpBindings implements Map<String, OtpErlangObject> {

    private final Map<String, OtpErlangObject> map;

    public OtpBindings() {
        map = new HashMap<String, OtpErlangObject>();
    }

    public OtpBindings(final OtpBindings binds) {
        this();
        merge(binds);
    }

    public void merge(final OtpBindings binds) {
        putAll(binds.asMap());
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

    public double getDouble(final String name) throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangDouble) {
            return ((OtpErlangDouble) r).doubleValue();
        }
        throw new OtpErlangException("value is not a double");
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
            final OtpErlangObject[] elems = ((OtpErlangList) r).elements();
            final ArrayList<OtpErlangObject> result = new ArrayList<OtpErlangObject>(
                    elems.length);
            Collections.addAll(result, elems);
            return result;
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

    public OtpErlangBinary getBinary(final String name)
            throws OtpErlangException {
        final OtpErlangObject r = get(name);
        if (r instanceof OtpErlangBinary) {
            return (OtpErlangBinary) r;
        }
        throw new OtpErlangException("value is not a binary");
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    public OtpErlangObject get(final Object key) {
        return map.get(key);
    }

    public OtpErlangObject put(final String key, final OtpErlangObject value) {
        return map.put(key, value);
    }

    public OtpErlangObject remove(final Object key) {
        return map.remove(key);
    }

    public void putAll(final Map<? extends String, ? extends OtpErlangObject> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<OtpErlangObject> values() {
        return map.values();
    }

    public Set<java.util.Map.Entry<String, OtpErlangObject>> entrySet() {
        return map.entrySet();
    }

    public Map<String, OtpErlangObject> asMap() {
        return Collections.unmodifiableMap(map);
    }

}
