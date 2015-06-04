package org.opendataspace.android.data;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.mapped.MappedCreate;
import com.j256.ormlite.stmt.mapped.MappedDelete;
import com.j256.ormlite.stmt.mapped.MappedUpdate;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;

public abstract class DaoBaseView<T extends ObjectBase> extends DaoBase<T> {

    private MappedCreate<T, Long> creator;
    private MappedUpdate<T, Long> updater;
    private MappedDelete<T, Long> deleter;
    EventDaoBase<T> event;

    DaoBaseView(ConnectionSource source, ObjectCache cache, Class<T> dataClass) throws SQLException {
        super(source, cache, dataClass);
        event = createEvent();
    }

    public void create(T val) throws SQLException {
        if (creator == null) {
            creator = MappedCreate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            int res = creator.insert(type, conn, val, cache);

            if (res > 0 && event != null) {
                event.addInsert(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }
    }

    public void update(T val) throws SQLException {
        if (updater == null) {
            updater = MappedUpdate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            int res = updater.update(conn, val, cache);

            if (res > 0 && event != null) {
                event.addUpdate(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }
    }

    public void delete(T val) throws SQLException {
        if (deleter == null) {
            deleter = MappedDelete.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            int res = deleter.delete(conn, val, cache);

            if (res > 0 && event != null) {
                event.addDelete(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }
    }

    public void createOrUpdate(T val) throws SQLException {
        if (val == null) {
            return;
        }

        if (exists(val)) {
            update(val);
        } else {
            create(val);
        }
    }

    void fire(DatabaseConnection conn) throws SQLException {
        if (conn.isAutoCommit() && event != null && !event.isEmpty()) {
            OdsApp.bus.post(event);
            event = createEvent();
        }
    }

    protected abstract EventDaoBase<T> createEvent();
}
