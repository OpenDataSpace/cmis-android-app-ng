package org.opendataspace.android.views;

import com.j256.ormlite.dao.CloseableIterator;
import de.greenrobot.event.EventBus;
import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.Task;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoEvent;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.ObjectBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewBase<T extends ObjectBase> implements CompatDisposable {

    private final List<T> data = new ArrayList<>();

    public ViewBase() {
        EventBus.getDefault().register(this);
    }

    protected boolean processEvent(DaoEvent<T> event) {
        List<DaoEvent.Event<T>> ls = event.getEvents();

        if (ls.isEmpty()) {
            return false;
        }

        for (DaoEvent.Event<T> cur : event.getEvents()) {
            final T object = cur.getObject();

            if (object == null) {
                continue;
            }

            switch (cur.getOperation()) {
            case INSERT:
                data.add(object);
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
            }
        }

        return true;
    }

    public void sync(final DaoBase<T> dao, final Account acc) {
        OdsApp.get().getPool().execute(new Task() {

            private CloseableIterator<T> it;

            @Override
            public void onExecute() throws Exception {
                it = iterate(dao, acc);
            }

            @Override
            public void onDone() throws Exception {
                if (it == null) {
                    return;
                }

                try {
                    data.clear();

                    while (it.hasNext()) {
                        data.add(it.nextThrow());
                    }
                } finally {
                    it.closeQuietly();
                }
            }
        });
    }

    protected List<T> getObjects() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public void dispose() {
        EventBus.getDefault().unregister(this);
    }

    protected CloseableIterator<T> iterate(DaoBase<T> dao, Account acc) throws SQLException {
        return dao.iterate();
    }
}
