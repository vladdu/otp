package com.ericsson.otp.erlang;

public interface IOtpConvertible {

    OtpErlangObject toErlangObject();

    void fromErlangObject(OtpErlangObject obj);
}
