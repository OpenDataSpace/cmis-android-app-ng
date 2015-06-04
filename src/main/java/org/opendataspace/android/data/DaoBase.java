package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.*;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;

public class DaoBase<T extends ObjectBase> {

    final ConnectionSource source;
    final TableInfo<T, Long> info;
    final DatabaseType type;
    final ObjectCache cache;

    private PreparedQuery<T> selectAll;
    private PreparedQuery<T> selectId;
    private String checker;
    private String countof;
    private SelectArg selectIdArg;

    DaoBase(ConnectionSource source, ObjectCache cache, Class<T> dataClass) throws SQLException {
        this.source = source;
        this.cache = cache;
        type = source.getDatabaseType();
        info = new TableInfo<>(source, null, dataClass);
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

        DatabaseConnection conn = source.getReadOnlyConnection();
        long res;

        try {
            res = conn.queryForLong(checker, new Object[]{id}, new FieldType[]{info.getIdField()});
        } finally {
            source.releaseConnection(conn);
        }

        return res != 0;
    }

    public long countOf() throws SQLException {
        if (countof == null) {
            QueryBuilder count = queryBuilder();
            count.selectRaw("COUNT(*)");
            countof = count.prepareStatementString();
        }

        DatabaseConnection conn = source.getReadOnlyConnection();
        long res;

        try {
            res = conn.queryForLong(countof);
        } finally {
            source.releaseConnection(conn);
        }

        return res;
    }

    public T get(long id) throws SQLException {
        if (id == ObjectBase.INVALID_ID) {
            return null;
        }

        if (selectId == null) {
            selectIdArg = new SelectArg(id);
            selectId = new QueryBuilder<>(type, info, null).where().eq(ObjectBase.FIELD_ID, selectIdArg).prepare();
        }

        selectIdArg.setValue(id);
        CloseableIterator<T> it = iterate(selectId);

        try {
            if (it.hasNext()) {
                return it.nextThrow();
            }
        } finally {
            it.closeQuietly();
        }

        return null;
    }

    DeleteBuilder<T, Long> deleteBuilder() {
        return new DeleteBuilder<>(type, info, null);
    }
}
