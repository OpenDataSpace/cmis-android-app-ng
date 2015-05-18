package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class ObjectBase {

    public static final String FIELD_ID = "id";

    public static final long INVALID_ID = -1;

    @Expose
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id = INVALID_ID;

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof ObjectBase)) {
            return false;
        }

        ObjectBase that = (ObjectBase) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    public boolean isValidId() {
        return id != INVALID_ID;
    }
}
