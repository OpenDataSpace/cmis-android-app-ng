package org.opendataspace.android.data;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import org.opendataspace.android.app.OdsApp;

import java.sql.SQLException;

public class DataGsonSerializer<T> extends BaseDataType {

    protected DataGsonSerializer(Class<T> cls) {
        super(SqlType.STRING, new Class[]{cls});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        T val = (T) javaObject;
        return val == null ? null : OdsApp.gson.toJson(val, getPrimaryClass());
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return OdsApp.gson.fromJson((String) sqlArg, getPrimaryClass());
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return defaultStr;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }
}
