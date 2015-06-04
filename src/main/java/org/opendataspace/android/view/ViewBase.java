package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ViewBase<T extends ObjectBase> implements CompatDisposable {

    private final List<T> data = new ArrayList<>();

    ViewBase() {
        OdsApp.bus.register(this, Event.PRIORITY_VIEW);
    }

    void processEvent(EventDaoBase<T> event) {
        for (EventDaoBase.Event<T> cur : event.getEvents()) {
            final T object = cur.getObject();

            if (object == null) {
                continue;
            }

            if (!isValid(object)) {
                data.remove(object);
                continue;
            }

            switch (cur.getOperation()) {
            case INSERT: {
                int pos = data.indexOf(object);

                if (pos != -1) {
                    data.set(pos, object);
                } else {
                    data.add(object);
                }
            }
            break;

            case DELETE:
                data.remove(object);
                break;

            case UPDATE: {
                int pos = data.indexOf(object);

                if (pos != -1) {
                    data.set(pos, object);
                }
            }
            break;

            case RESET:
                if (shouldReset(cur.getExtra())) {
                    data.clear();
                }
                break;
            }
        }
    }

    public void sync(final DaoBase<T> dao) throws SQLException {
        data.clear();

        CloseableIterator<T> it = null;

        try {
            it = iterate(dao);

            if (it == null) {
                return;
            }

            while (it.hasNext()) {
                data.add(it.nextThrow());
            }
        } finally {
            if (it != null) {
                it.closeQuietly();
            }

            notifyAdapters();
        }
    }

    public List<T> getObjects() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public void dispose() {
        OdsApp.bus.unregister(this);
    }

    CloseableIterator<T> iterate(DaoBase<T> dao) throws SQLException {
        return dao.iterate();
    }

    public int getCount() {
        return data.size();
    }

    boolean isValid(T val) {
        return true;
    }

    boolean shouldReset(long extra) {
        return true;
    }

    protected abstract void notifyAdapters();
}
