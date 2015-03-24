package org.opendataspace.android.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.GenericRowMapper;

public abstract class DataAdapter<T> extends CursorAdapter {

    private GenericRowMapper<T> mapper;
    private AndroidDatabaseResults data;
    private final LayoutInflater inflater;
    private final int layoutId;
    private final String idColumn;

    protected DataAdapter(Context context, CloseableIterator<T> data, DaoBase<T, ?> dao, int layoutId) {
        super(context, null, false);

        try {
            this.mapper = dao.getSelectStarRowMapper();
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.idColumn = dao.getIdColumnName();

        swapResults(data);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        try {
            bindView(context, view, mapper.mapRow(data));
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layoutId, parent, false);
    }

    protected abstract void bindView(Context context, View view, T item);

    public void swapResults(CloseableIterator<T> data) {
        this.data = data != null ? (AndroidDatabaseResults) data.getRawResults() : null;
        swapCursor(data != null ? new DataAdapterCursor(this.data.getRawCursor(), idColumn) : null);
    }
}
