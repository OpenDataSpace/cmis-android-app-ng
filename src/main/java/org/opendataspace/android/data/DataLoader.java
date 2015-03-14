package org.opendataspace.android.data;

import android.content.Context;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.TaskLoader;

public class DataLoader<T> extends TaskLoader<CloseableIterator<T>> {

    private final DaoBase<T, ?> dao;
    private final ForceLoadContentObserver observer = new ForceLoadContentObserver();
    private CloseableIterator<T> cursor;

    public DataLoader(Context context, DaoBase<T, ?> dao) {
        super(context);
        this.dao = dao;
    }

    @Override
    public CloseableIterator<T> loadInBackground() throws Exception {
        CloseableIterator<T> cursor = dao.iterator();
        AndroidDatabaseResults results = (AndroidDatabaseResults) cursor.getRawResults();
        results.getRawCursor().registerContentObserver(observer);
        return cursor;
    }

    @Override
    public void deliverResult(CloseableIterator<T> cursor) {
        if (isReset()) {
            closeCursor(cursor);
            return;
        }

        if (this.cursor != cursor) {
            closeCursor(this.cursor);
            this.cursor = cursor;
        }

        if (isStarted()) {
            super.deliverResult(cursor);
        }
    }

    private void closeCursor(CloseableIterator<T> cursor) {
        if (cursor != null) {
            cursor.closeQuietly();
        }
    }

    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
        closeCursor(cursor);
        cursor = null;
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}
