package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoRepo;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class DaoRepo extends DaoBase<Repo> {

    private PreparedQuery<Repo> byAccount;
    private SelectArg byAccountArg;

    DaoRepo(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, Repo.class);

    }

    public CloseableIterator<Repo> forAccount(Account account) throws SQLException {
        if (byAccount == null) {
            byAccountArg = new SelectArg();
            byAccount = queryBuilder().where().eq(Repo.FIELD_ACCID, byAccountArg).and()
                    .ne(Repo.FIELD_TYPE, Repo.Type.CONFIG).prepare();
        }

        byAccountArg.setValue(account.getId());
        return iterate(byAccount);
    }

    @Override
    protected EventDaoBase<Repo> createEvent() {
        return new EventDaoRepo();
    }

    public Repo getConfig(Account account) throws SQLException {
        CloseableIterator<Repo> it =
                iterate(queryBuilder().limit(1l).where().eq(Repo.FIELD_ACCID, account.getId()).and()
                        .eq(Repo.FIELD_TYPE, Repo.Type.CONFIG).prepare());

        try {
            return it.hasNext() ? it.nextThrow() : null;
        } finally {
            it.close();
        }
    }
}
