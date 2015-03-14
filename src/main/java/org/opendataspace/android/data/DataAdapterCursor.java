package org.opendataspace.android.data;

import android.database.Cursor;
import android.database.CursorWrapper;

class DataAdapterCursor extends CursorWrapper {

    private final int idColumnIndex;

    public DataAdapterCursor(Cursor cursor, String idColumnName) {
        super(cursor);
        idColumnIndex = cursor.getColumnIndex(idColumnName);
    }

    @Override
    public int getColumnIndex(String columnName) {
        if ("_id".equals(columnName)) {
            return idColumnIndex;
        }

        return super.getColumnIndex(columnName);
    }


    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        if ("_id".equals(columnName)) {
            return idColumnIndex;
        }

        return super.getColumnIndexOrThrow(columnName);
    }
}

