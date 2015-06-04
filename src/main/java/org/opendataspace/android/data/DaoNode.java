package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class DaoNode extends DaoBaseView<Node> {

    private PreparedQuery<Node> byParent;
    private SelectArg byParentPidArg;
    private SelectArg byParentRidArg;

    DaoNode(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, Node.class);
    }

    @Override
    protected EventDaoBase<Node> createEvent() {
        return new EventDaoNode();
    }

    public CloseableIterator<Node> forParent(Repo repo, long parentId) throws SQLException {
        if (byParent == null) {
            byParentPidArg = new SelectArg();
            byParentRidArg = new SelectArg();
            byParent =
                    queryBuilder().where().eq(Node.FIELD_RID, byParentRidArg).and().eq(Node.FIELD_PID, byParentPidArg)
                            .prepare();
        }

        byParentPidArg.setValue(parentId);
        byParentRidArg.setValue(repo.getId());
        return iterate(byParent);
    }

    public void deleteByRepo(Repo repo) throws SQLException {
        DatabaseConnection conn = source.getReadWriteConnection();
        CompiledStatement stmt = null;

        try {
            long id = repo.getId();
            stmt = deleteBuilder().where().eq(Node.FIELD_RID, id).prepare().compile(conn, StatementBuilder.StatementType.DELETE);
            int res = stmt.runUpdate();

            if (res > 0 && event != null) {
                event.addReset(id);
                fire(conn);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            source.releaseConnection(conn);
        }
    }
}
