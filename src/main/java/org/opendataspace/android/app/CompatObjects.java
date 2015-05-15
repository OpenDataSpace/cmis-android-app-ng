package org.opendataspace.android.app;

public class CompatObjects {

    public static boolean equals(Object a, Object b) {
        return (a == null && b == null) || (a != null && b != null && a.equals(b));
    }
}
