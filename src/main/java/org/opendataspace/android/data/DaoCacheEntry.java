package org.opendataspace.android.data;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import org.opendataspace.android.object.CacheEntry;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class DaoCacheEntry extends DaoBaseSimple<CacheEntry> {

    DaoCacheEntry(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, CacheEntry.class);
    }

    public void deleteByRepo(Repo repo) throws SQLException {
        DatabaseConnection conn = source.getReadWriteConnection();
        CompiledStatement stmt = null;

        try {
            stmt = deleteBuilder().where().eq(CacheEntry.FIELD_REPID, repo.getId()).prepare().
                    compile(conn, StatementBuilder.StatementType.DELETE);

            stmt.runUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            source.releaseConnection(conn);
        }
    }
}
