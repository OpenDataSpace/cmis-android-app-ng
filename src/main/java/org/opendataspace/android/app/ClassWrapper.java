package org.opendataspace.android.app;

public class ClassWrapper {

    private final Class<?> info;
    private final Object data;

    public ClassWrapper(Object data) {
        this.info = data != null ? data.getClass() : null;
        this.data = data;
    }

    ClassWrapper(Object val, Class<?> cls) {
        this.info = cls;
        this.data = val;
    }

    public Object getObject() {
        return data;
    }

    public Class<?> getClassInfo() {
        return info;
    }

    public boolean isEmpty() {
        return info == null;
    }
}
