package org.opendataspace.android.objects;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class ObjectBase {

    @Expose
    @DatabaseField(generatedId = true, columnName = "id")
    private long id = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        return id != -1;
    }
}
