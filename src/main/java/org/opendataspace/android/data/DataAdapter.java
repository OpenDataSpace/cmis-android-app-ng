package org.opendataspace.android.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.stmt.GenericRowMapper;

import java.sql.SQLException;

public abstract class DataAdapter<T> extends CursorAdapter {

    private final GenericRowMapper<T> mapper;
    private AndroidDatabaseResults data;

    protected DataAdapter(Context context, AndroidDatabaseResults data, DaoBase<T, ?> dao) throws SQLException {
        super(context, new DataAdapterCursor(data.getRawCursor(), dao.getIdColumnName()), false);
        this.mapper = dao.getSelectStarRowMapper();
        this.data = data;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        try {
            bindView(view, context, mapper.mapRow(data));
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }
    }

    public abstract void bindView(View itemView, Context context, T item);

    public void resetCursor() {
        swapCursor(null);
        this.data = null;
    }
}
