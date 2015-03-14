package org.opendataspace.android.data;

import android.content.Context;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.support.ConnectionSource;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class DaoBase<T, ID> extends BaseDaoImpl<T, ID> {

    private final List<WeakReference<DataLoader<T>>> loaders = new ArrayList<>();

    DaoBase(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public DataLoader<T> getSQLCursorLoader(Context context) {
        DataLoader<T> loader = new DataLoader<>(context, this);

        synchronized (loaders) {
            loaders.add(new WeakReference<>(loader));
        }

        return loader;
    }

    void notifyContentChange() {
        synchronized (loaders) {
            for (Iterator<WeakReference<DataLoader<T>>> itr = loaders.iterator(); itr.hasNext(); ) {
                WeakReference<DataLoader<T>> weakRef = itr.next();
                DataLoader<T> loader = weakRef.get();

                if (loader == null) {
                    itr.remove();
                } else {
                    loader.onContentChanged();
                }
            }
        }
    }

    @Override
    public int create(T data) throws SQLException {
        int result = super.create(data);

        if (result > 0) {
            notifyContentChange();
        }

        return result;
    }

    @Override
    public int updateRaw(String statement, String... arguments) throws SQLException {
        int result = super.updateRaw(statement, arguments);

        if (result > 0) {
            notifyContentChange();
        }

        return result;
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        int result = super.delete(preparedDelete);

        if (result > 0) {
            notifyContentChange();
        }

        return result;
    }

    public String getIdColumnName() {
        return tableInfo.getIdField().getColumnName();
    }
}
