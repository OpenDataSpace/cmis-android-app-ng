package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.SelectIterator;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.mapped.MappedCreate;
import com.j256.ormlite.stmt.mapped.MappedDelete;
import com.j256.ormlite.stmt.mapped.MappedUpdate;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import de.greenrobot.event.EventBus;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;

public abstract class DaoBase<T extends ObjectBase> {

    private final ConnectionSource source;
    private final TableInfo<T, Long> info;
    private final DatabaseType type;
    private final ObjectCache cache;

    private MappedCreate<T, Long> creator;
    private MappedUpdate<T, Long> updater;
    private MappedDelete<T, Long> deleter;
    private PreparedQuery<T> selectAll;
    private String checker;
    private String countof;
    private EventDaoBase<T> event;

    DaoBase(ConnectionSource source, ObjectCache cache, Class<T> dataClass) throws SQLException {
        this.source = source;
        this.cache = cache;
        type = source.getDatabaseType();
        info = new TableInfo<>(source, null, dataClass);
        event = createEvent();
    }

    public int create(T val) throws SQLException {
        if (creator == null) {
            creator = MappedCreate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();
        int res;

        try {
            res = creator.insert(type, conn, val, cache);

            if (res > 0) {
                event.addInsert(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }

        return res;
    }

    public int update(T val) throws SQLException {
        if (updater == null) {
            updater = MappedUpdate.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();
        int res;

        try {
            res = updater.update(conn, val, cache);

            if (res > 0) {
                event.addUpdate(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }

        return res;
    }

    public int delete(T val) throws SQLException {
        if (deleter == null) {
            deleter = MappedDelete.build(type, info);
        }

        DatabaseConnection conn = source.getReadWriteConnection();
        int res;

        try {
            res = deleter.delete(conn, val, cache);

            if (res > 0) {
                event.addDelete(val);
                fire(conn);
            }
        } finally {
            source.releaseConnection(conn);
        }

        return res;
    }

    public CloseableIterator<T> iterate() throws SQLException {
        if (selectAll == null) {
            selectAll = new QueryBuilder<>(type, info, null).prepare();
        }

        return iterate(selectAll);
    }

    public CloseableIterator<T> iterate(PreparedQuery<T> querry) throws SQLException {
        DatabaseConnection conn = source.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        SelectIterator<T, Long> it;

        try {
            compiledStatement = querry.compile(conn, StatementBuilder.StatementType.SELECT, -1);
            it = new SelectIterator<>(info.getDataClass(), null, querry, source, conn, compiledStatement,
                    querry.getStatement(), cache);
            conn = null;
            compiledStatement = null;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }

            if (conn != null) {
                source.releaseConnection(conn);
            }
        }

        return it;
    }

    public QueryBuilder<T, Long> queryBuilder() {
        return new QueryBuilder<>(type, info, null);
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

    private Object extractId(T val) throws SQLException {
        FieldType field = info.getIdField();

        if (field == null) {
            throw new SQLException("Class " + info.getDataClass() + " does not have an id field");
        }

        return field.extractJavaFieldValue(val);
    }

    public boolean exists(T val) throws SQLException {
        if (val == null || !val.isValidId()) {
            return false;
        }

        Object id = extractId(val);

        if (id == null) {
            return false;
        }

        if (checker == null) {
            QueryBuilder count = queryBuilder();
            count.selectRaw("COUNT(*)").where().eq(info.getIdField().getColumnName(), new SelectArg());
            checker = count.prepareStatementString();
        }

        DatabaseConnection conn = source.getReadWriteConnection();
        long res;

        try {
            res = conn.queryForLong(checker, new Object[] {id}, new FieldType[] {info.getIdField()});
        } finally {
            source.releaseConnection(conn);
        }

        return res != 0;
    }

    protected void fire(DatabaseConnection conn) throws SQLException {
        if (conn.isAutoCommit() && !event.isEmpty()) {
            OdsApp.bus.post(event);
            event = createEvent();
        }
    }

    public long countOf() throws SQLException {
        if (countof == null) {
            QueryBuilder count = queryBuilder();
            count.selectRaw("COUNT(*)");
            countof = count.prepareStatementString();
        }

        DatabaseConnection conn = source.getReadWriteConnection();
        long res;

        try {
            res = conn.queryForLong(countof);
        } finally {
            source.releaseConnection(conn);
        }

        return res;
    }

    protected abstract EventDaoBase<T> createEvent();
}
