package org.opendataspace.android.data;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.mapped.MappedCreate;
import com.j256.ormlite.stmt.mapped.MappedDelete;
import com.j256.ormlite.stmt.mapped.MappedUpdate;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;

public class DaoBaseSimple<T extends ObjectBase> extends DaoBase<T> {

    private MappedCreate<T, Long> creator;
    private MappedUpdate<T, Long> updater;
    private MappedDelete<T, Long> deleter;

    DaoBaseSimple(ConnectionSource source, ObjectCache cache, Class<T> dataClass) throws SQLException {
        super(source, cache, dataClass);
    }

    void create(T val) throws SQLException {
        if (creator == null) {
            creator = MappedCreate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            creator.insert(type, conn, val, cache);
        } finally {
            source.releaseConnection(conn);
        }
    }

    private void update(T val) throws SQLException {
        if (updater == null) {
            updater = MappedUpdate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            updater.update(conn, val, cache);
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
            deleter.delete(conn, val, cache);
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
}
