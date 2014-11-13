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
 * Provides a Java representation of a cons cell (works for both lists and
 * tuples!). A list or tuple pattern can end with " | Variable", where the
 * variable is matched against the remaining elements.
 * <p>
 * <b>Note: These are to NOT to be sent to an Erlang node !!!!</b> Their use is
 * in pattern matching only.
 */
@Beta
public class OtpPatternCons extends OtpErlangObject {

    private static final long serialVersionUID = -1L;

    public OtpPatternCons() {
    }

    @Override
    public String toString() {
        return "|";
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OtpPatternCons)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return "|".hashCode();
    }

    @Override
    public void encode(final OtpOutputStream arg0) {
        // throw new NotImplementedException();
    }

}
