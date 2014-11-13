/*
 * %CopyrightBegin%
 *
 * Copyright Ericsson AB 2014. All Rights Reserved.
 *
 * The contents of this file are subject to the Erlang Public License,
 * Version 1.1, (the "License"); you may not use this file except in
 * compliance with the License. You should have received a copy of the
 * Erlang Public License along with this software. If not, it can be
 * retrieved online at http://www.erlang.org/.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * %CopyrightEnd%
 */
package com.ericsson.otp.erlang;

/**
 * Provides a Java representation of Erlang format placeholders.
 * <p>
 * <b>Note: These are to NOT to be sent to an Erlang node !!!!</b> Their use is
 * in formatting only.
 */
@Beta
public class OtpFormatPlaceholder extends OtpErlangObject {

    private static final long serialVersionUID = -1L;

    private final String name;

    public OtpFormatPlaceholder(final String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "~" + name;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OtpFormatPlaceholder)) {
            return false;
        }

        final OtpFormatPlaceholder l = (OtpFormatPlaceholder) o;
        return name.equals(l.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void encode(final OtpOutputStream arg0) {
        // throw new NotImplementedException();
    }

}
