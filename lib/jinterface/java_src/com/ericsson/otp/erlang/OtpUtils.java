package com.ericsson.otp.erlang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OtpUtils {

    public static <T> Collection<T> asCollection(final T... elements) {
        final ArrayList<T> list = new ArrayList<T>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

}
