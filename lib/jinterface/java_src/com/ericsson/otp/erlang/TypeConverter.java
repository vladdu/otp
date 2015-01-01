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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Helps converting Java values to Erlang terms, and back. The type information
 * is provided through a string signature, as below.
 *
 * <dl>
 * <dt>x</dt>
 * <dd>Uses simple conversion, complex types are expected to be OtpErlangObjecs
 * already.</dd>
 * <dt>i</dt>
 * <dd>integer</dd>
 * <dt>s</dt>
 * <dd>string</dd>
 * <dt>a</dt>
 * <dd>atom</dd>
 * <dt>d</dt>
 * <dd>float</dd>
 * <dt>p</dt>
 * <dd>pid</dd>
 * <dt>r</dt>
 * <dd>reference</dd>
 * <dt>j</dt>
 * <dd>java reference (a distinguished reference, to be used with e->j rpcs)</dd>
 * <dt>m</dt>
 * <dd>map</dd>
 * <dt>l*</dt>
 * <dd>list, the next type descriptor specifies the items' type</dd>
 * <dt>f</dt>
 * <dd>fun -- currently not implemented</dd>
 * <dt>o</dt>
 * <dd>boolean (the atoms true/false)</dd>
 * <dt>0-9</dt>
 * <dd>tuple, the number is the arity and the types of the elements follow in
 * order. Only arities between 0 and 9 are supported.</dd>
 * </dl>
 *
 */
public final class TypeConverter {

    private static final String CANT_CONVERT_TO = ", can't convert to ";
    private static final String WRONG_ARG_TYPE = "wrong arg type ";

    public static Class<?> getClassByName(final String arg) {
        if ("char".equals(arg)) {
            return char.class;
        }
        if ("byte".equals(arg)) {
            return byte.class;
        }
        if ("short".equals(arg)) {
            return short.class;
        }
        if ("int".equals(arg)) {
            return int.class;
        }
        if ("long".equals(arg)) {
            return long.class;
        }
        if ("boolean".equals(arg)) {
            return boolean.class;
        }
        if ("float".equals(arg)) {
            return float.class;
        }
        if ("double".equals(arg)) {
            return double.class;
        }
        try {
            return Class.forName(arg);
        } catch (final ClassNotFoundException e) {
            return Object.class;
        }
    }

    public static Class<?> javaType2erlang(final Class<?> obj) {
        if (obj.isArray()) {
            return OtpErlangTuple.class;
        }
        if (Iterable.class.isAssignableFrom(obj)) {
            return OtpErlangList.class;
        }
        if (Map.class.isAssignableFrom(obj)) {
            return OtpErlangMap.class;
        }
        if (obj == Integer.TYPE) {
            return OtpErlangLong.class;
        }
        if (obj == Long.TYPE) {
            return OtpErlangLong.class;
        }
        if (obj == Boolean.TYPE) {
            return OtpErlangAtom.class;
        }
        if (obj == Double.TYPE) {
            return OtpErlangDouble.class;
        }
        if (obj == String.class) {
            return OtpErlangString.class;
        }
        if (obj == Long.class) {
            return OtpErlangLong.class;
        }
        if (obj == Integer.class) {
            return OtpErlangLong.class;
        }
        if (obj == Double.class) {
            return OtpErlangDouble.class;
        }
        if (obj == Boolean.class) {
            return OtpErlangAtom.class;
        }
        return OtpErlangRef.class;

    }

    @SuppressWarnings("boxing")
    public static Object erlang2java(final OtpErlangObject obj,
            final Class<?> cls) throws SignatureException {
        try {
            if (cls == obj.getClass()) {
                return obj;
            }
            // if the conversion method exists, use it
            try {
                final Method method = cls.getMethod("fromErlangObject",
                        new Class<?>[] { OtpErlangObject.class });
                method.setAccessible(true);
                final Object o = method.invoke(null, obj);
                return o;
            } catch (final NoSuchMethodException e) {
                // ignore, continue
            }

            if (cls.isArray()) {
                return cvtArray(obj, cls);
            }

            if (cls == String.class) {
                return cvtString(obj);
            }
            if (isNumericClass(cls)) {
                if (obj instanceof OtpErlangLong) {
                    final long res = ((OtpErlangLong) obj).longValue();
                    if (cls == char.class || cls == Character.class) {
                        return (char) res;
                    }
                    if (cls == int.class || cls == Integer.class) {
                        return (int) res;
                    }
                    if (cls == byte.class || cls == Byte.class) {
                        return (byte) res;
                    }
                    if (cls == short.class || cls == Short.class) {
                        return (short) res;
                    }
                    if (cls == long.class || cls == Long.class) {
                        return res;
                    }
                }
                throw new SignatureException(WRONG_ARG_TYPE
                        + obj.getClass().getName() + CANT_CONVERT_TO
                        + cls.getCanonicalName());
            }
            if (cls == boolean.class || cls == Boolean.class) {
                if (obj instanceof OtpErlangAtom) {
                    final String s = ((OtpErlangAtom) obj).atomValue();
                    if ("true".equals(s)) {
                        return true;
                    }
                    if ("false".equals(s)) {
                        return false;
                    }
                }
                throw new SignatureException(WRONG_ARG_TYPE
                        + obj.getClass().getName() + CANT_CONVERT_TO
                        + cls.getCanonicalName());
            }
            if (Map.class.isAssignableFrom(cls)) {
                if (obj instanceof OtpErlangMap) {
                    final Map<Object, Object> result = new HashMap<Object, Object>();
                    final OtpErlangMap map = (OtpErlangMap) obj;
                    for (final OtpErlangObject key : map.keys()) {
                        final OtpErlangObject value = map.get(key);
                        result.put(erlang2java(key, key.getClass()),
                                erlang2java(value, value.getClass()));
                    }
                    return result;
                }
                throw new SignatureException(WRONG_ARG_TYPE
                        + obj.getClass().getName() + CANT_CONVERT_TO
                        + cls.getCanonicalName());
            }
            if (Iterable.class.isAssignableFrom(cls)) {
                if (obj instanceof OtpErlangList) {
                    final OtpErlangObject[] list = ((OtpErlangList) obj)
                            .elements();
                    final Object[] olist = new Object[list.length];
                    for (int i = 0; i < list.length; i++) {
                        olist[i] = erlang2java(list[i], list[i].getClass());
                    }
                    return Arrays.asList(olist);
                }
                throw new SignatureException(WRONG_ARG_TYPE
                        + obj.getClass().getName() + CANT_CONVERT_TO
                        + cls.getCanonicalName());
            }
            if (obj instanceof OtpErlangRef) {
                throw new SignatureException(WRONG_ARG_TYPE
                        + obj.getClass().getName() + CANT_CONVERT_TO
                        + cls.getCanonicalName());
            }
            return obj;
        } catch (final SignatureException e) {
            throw e;
        } catch (final Exception e) {
            throw new SignatureException(e);
        }
    }

    private static boolean isNumericClass(final Class<?> cls) {
        return cls == char.class || cls == Character.class || cls == int.class
                || cls == Integer.class || cls == byte.class
                || cls == Byte.class || cls == short.class
                || cls == Short.class || cls == long.class || cls == Long.class;
    }

    private static String cvtString(final OtpErlangObject obj)
            throws SignatureException {
        if (obj instanceof OtpErlangString) {
            return ((OtpErlangString) obj).stringValue();
        }
        if (obj instanceof OtpErlangAtom) {
            return ((OtpErlangAtom) obj).atomValue();
        }
        if (obj instanceof OtpErlangBinary) {
            return new String(((OtpErlangBinary) obj).binaryValue());
        }
        if (obj instanceof OtpErlangList) {
            final StringBuilder res = new StringBuilder();
            for (final OtpErlangObject el : (OtpErlangList) obj) {
                if (el instanceof OtpErlangLong) {
                    final long l = ((OtpErlangLong) el).longValue();
                    res.append((char) (l & 0xFFFF));
                } else {
                    res.append(erlang2java(el, String.class));
                }
            }
            return res.toString();
        }
        throw new SignatureException(WRONG_ARG_TYPE + obj.getClass().getName()
                + CANT_CONVERT_TO + "String");
    }

    private static Object cvtArray(final OtpErlangObject obj, final Class<?> cls)
            throws SignatureException {
        OtpErlangObject[] els = null;
        if (obj instanceof OtpErlangList) {
            els = ((OtpErlangList) obj).elements();
        }
        if (obj instanceof OtpErlangTuple) {
            els = ((OtpErlangTuple) obj).elements();
        }
        if (els != null) {
            final Object arr = Array.newInstance(cls.getComponentType(),
                    els.length);
            for (int i = 0; i < els.length; i++) {
                Array.set(arr, i, erlang2java(els[i], cls.getComponentType()));
            }
            return arr;
        }
        if (obj instanceof OtpErlangString) {
            final byte[] s = ((OtpErlangString) obj).stringValue().getBytes();
            final Object arr = Array.newInstance(cls.getComponentType(),
                    s.length);

            for (int i = 0; i < s.length; i++) {
                Array.set(arr, i, s[i]);
            }
            return arr;
        }
        return new Object[0];
    }

    /**
     * Converts Java objects to Erlang terms.<br/>
     *
     * @param obj
     *            the object to be converted
     * @param type
     *            the desired result's type
     * @return
     * @throws ConversionException
     */
    public static OtpErlangObject java2erlang(final Object obj,
            final String type) throws SignatureException {
        return java2erlang(obj, Signature.parse(type)[0]);
    }

    @SuppressWarnings("boxing")
    public static OtpErlangObject java2erlang(final Object obj,
            final Signature type) throws SignatureException {
        if (type.kind == 'x') {
            return java2erlang(obj);
        }
        if (obj instanceof String) {
            return cvtString(obj, type);
        }
        if (obj instanceof Character) {
            if (type.kind == 'i') {
                return new OtpErlangChar((Character) obj);
            }
            failConversion(obj, type);
        }
        if (obj instanceof Number) {
            return cvtNumber(obj, type);
        }
        if (obj instanceof Boolean) {
            if (type.kind == 'o') {
                return new OtpErlangAtom((Boolean) obj ? "true" : "false");
            }
            failConversion(obj, type);
        }
        if (obj instanceof Iterable<?>) {
            if (type.kind == 'l') {
                final Iterable<?> v = (Iterable<?>) obj;
                final List<OtpErlangObject> vv = new ArrayList<OtpErlangObject>();
                for (final Object elem : v) {
                    vv.add(java2erlang(elem, type.content[0]));
                }
                return new OtpErlangList(vv.toArray(new OtpErlangObject[vv
                        .size()]));
            }
            failConversion(obj, type);
        }
        if (obj instanceof Map<?, ?>) {
            if (type.kind == 'm') {
                @SuppressWarnings("unchecked")
                final Map<OtpErlangObject, OtpErlangObject> map = (Map<OtpErlangObject, OtpErlangObject>) obj;
                final int size = map.keySet().size();
                final OtpErlangObject[] keys = map.keySet().toArray(
                        new OtpErlangObject[size]);
                final OtpErlangObject[] values = new OtpErlangObject[size];
                for (int i = 0; i < size; i++) {
                    values[i] = map.get(keys[i]);
                }
                return new OtpErlangMap(keys, values);
            }
            failConversion(obj, type);
        }

        if (obj instanceof OtpErlangPid) {
            return (OtpErlangPid) obj;
        }
        if (obj instanceof OtpErlangRef) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangBinary) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangObject) {
            checkConversion(obj);
            return (OtpErlangObject) obj;
        }
        if (obj instanceof IConvertible) {
            return ((IConvertible) obj).toErlangObject();
        }

        if (obj != null && obj.getClass().isArray()) {
            final int len = Array.getLength(obj);
            // Class<?> component = obj.getClass().getComponentType();
            if (type.kind == 'b') {
                // TODO we can convert more things to binaries
                return new OtpErlangBinary(obj);
            }
            if (type.kind == 'l') {
                final OtpErlangObject[] vv = new OtpErlangObject[len];
                for (int i = 0; i < len; i++) {
                    vv[i] = java2erlang(Array.get(obj, i), type.content[0]);
                }
                return new OtpErlangList(vv);
            } else if (type.kind == 't') {
                final OtpErlangObject[] vv = new OtpErlangObject[len];
                for (int i = 0; i < len; i++) {
                    vv[i] = java2erlang(Array.get(obj, i), type.content[i]);
                }
                return new OtpErlangTuple(vv);
            } else {
                failConversion(obj, type);
            }
        }

        if (type.kind == 's' && obj != null) {
            return new OtpErlangString(obj.toString());
        }
        if (type.kind == 'b' && obj != null) {
            return new OtpErlangBinary(obj.toString().getBytes());
        }
        failConversion(obj, type);
        return null;
    }

    private static void checkConversion(final Object obj) {
        if (TypeConverter.willCheckConversion()) {
            final StackTraceElement[] st = new Throwable().getStackTrace();
            final StackTraceElement el = findRpcStacktraceElement(st);
            System.out.println(" *** deprecated use of java2erlang: "
                    + obj.getClass().getSimpleName() + " " + el);
            if (el == null) {
                System.out.println("$$$");
                for (final StackTraceElement ste : st) {
                    System.out.println("   " + ste);
                }
            }
        }
    }

    private static StackTraceElement findRpcStacktraceElement(
            final StackTraceElement[] st) {
        boolean found = false;
        for (final StackTraceElement ste : st) {
            if (found) {
                if (!isRelevantMethod(ste)) {
                    return ste;
                }
            }
            if (isRelevantMethod(ste)) {
                found = true;
            }
        }
        return null;
    }

    private static boolean isRelevantMethod(final StackTraceElement ste) {
        return (ste.getMethodName().equals("send")
                || ste.getMethodName().equals("sendRpc")
                || ste.getMethodName().equals("rpc")
                || ste.getMethodName().equals("rpct")
                || ste.getMethodName().equals("rpcx") || ste.getMethodName()
                .equals("rpcxt")) && ste.getClassName().endsWith("Backend");
    }

    private static OtpErlangObject cvtNumber(final Object obj,
            final Signature type) throws SignatureException {
        if (obj instanceof Float) {
            if (type.kind == 'd') {
                return new OtpErlangFloat((Float) obj);
            }
            failConversion(obj, type);
        } else if (obj instanceof Double) {
            if (type.kind == 'd') {
                return new OtpErlangDouble((Double) obj);
            }
            failConversion(obj, type);
        } else if (type.kind == 'i') {
            if (obj instanceof BigInteger) {
                return new OtpErlangLong((BigInteger) obj);
            }
            return new OtpErlangLong(((Number) obj).longValue());
        } else {
            failConversion(obj, type);
        }
        return null;
    }

    private static OtpErlangObject cvtString(final Object obj,
            final Signature type) throws SignatureException {
        if (type.kind == 's') {
            return new OtpErlangString((String) obj);
        } else if (type.kind == 'a') {
            return new OtpErlangAtom((String) obj);
        } else if (type.kind == 'b') {
            return new OtpErlangBinary(((String) obj).getBytes());
        } else {
            failConversion(obj, type);
        }
        return null;
    }

    /**
     * Old style java->erlang conversion, used when "x" is given as an argument.
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("boxing")
    private static OtpErlangObject java2erlang(final Object obj) {
        if (obj instanceof String) {
            return new OtpErlangString((String) obj);
        }
        if (obj instanceof Character) {
            return new OtpErlangChar((Character) obj);
        }
        if (obj instanceof Byte) {
            return new OtpErlangByte((Byte) obj);
        }
        if (obj instanceof Short) {
            return new OtpErlangShort((Short) obj);
        }
        if (obj instanceof Integer) {
            return new OtpErlangInt((Integer) obj);
        }
        if (obj instanceof Long) {
            return new OtpErlangLong((Long) obj);
        }
        if (obj instanceof BigInteger) {
            return new OtpErlangLong((BigInteger) obj);
        }
        if (obj instanceof Float) {
            return new OtpErlangFloat((Float) obj);
        }
        if (obj instanceof Double) {
            return new OtpErlangDouble((Double) obj);
        }
        if (obj instanceof Boolean) {
            return new OtpErlangAtom((Boolean) obj ? "true" : "false");
        }
        if (obj instanceof Iterable<?>) {
            final Iterable<?> v = (Iterable<?>) obj;
            final List<OtpErlangObject> vv = new ArrayList<OtpErlangObject>();
            for (final Object elem : v) {
                vv.add(java2erlang(elem));
            }
            return new OtpErlangList(vv.toArray(new OtpErlangObject[vv.size()]));
        }
        if (obj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            final Object[] k = map.keySet().toArray(
                    new Object[map.keySet().size()]);
            final OtpErlangObject[] kk = new OtpErlangObject[k.length];
            final OtpErlangObject[] vv = new OtpErlangObject[k.length];
            for (int i = 0; i < k.length; i++) {
                kk[i] = java2erlang(k[i]);
                vv[i] = java2erlang(map.get(k[i]));
            }
            return new OtpErlangMap(kk, vv);
        }

        if (obj instanceof OtpErlangPid) {
            return (OtpErlangPid) obj;
        }
        if (obj instanceof OtpErlangRef) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangTuple) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangAtom) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangBinary) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangString) {
            return (OtpErlangObject) obj;
        }
        if (obj instanceof OtpErlangObject) {
            checkConversion(obj);
            return (OtpErlangObject) obj;
        }

        if (obj != null && obj.getClass().isArray()) {
            final int len = Array.getLength(obj);
            final OtpErlangObject[] vv = new OtpErlangObject[len];
            for (int i = 0; i < len; i++) {
                vv[i] = java2erlang(Array.get(obj, i));
            }
            return new OtpErlangList(vv);
        }
        return null;
    }

    private static void failConversion(final Object obj, final Signature type)
            throws SignatureException {
        throw new SignatureException(String.format(
                "Bad conversion required: %s(%s) - %s", obj.getClass()
                        .getName(), obj.toString(), type.toString()));
    }

    public static boolean willCheckConversion() {
        final String dev = System.getProperty("erlide.test_rpc");
        return Boolean.parseBoolean(dev);
    }

    /**
     * @noreference This method is not intended to be referenced by clients.
     */
    public static boolean doesMatchSignature(final OtpErlangObject term,
            final Signature signature) {
        if (signature.kind == 'x') {
            return true;
        }
        if (term instanceof OtpErlangAtom) {
            return signature.kind == 'a';
        }
        if (term instanceof OtpErlangLong) {
            return signature.kind == 'i';
        }
        if (term instanceof OtpErlangPid) {
            return signature.kind == 'p';
        }
        return false;
    }

    public static OtpErlangObject mapToProplist(
            final Map<String, OtpErlangObject> map) {
        final Set<Entry<String, OtpErlangObject>> v = map.entrySet();
        final OtpErlangObject[] vv = new OtpErlangObject[v.size()];
        int i = 0;
        for (final Entry<String, OtpErlangObject> entry : v) {
            final OtpErlangAtom key = new OtpErlangAtom(entry.getKey());
            final OtpErlangObject value = entry.getValue();
            vv[i] = OtpErlang.mkTuple(key, value);
            i++;
        }
        return new OtpErlangList(vv);
    }
}
