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

    private final PreparedQuery<Repo> byAccount;
    private final SelectArg byAccountArg;

    DaoRepo(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, Repo.class);
        byAccountArg = new SelectArg();
        byAccount = queryBuilder().where().eq(Repo.FIELD_ACCID, byAccountArg).prepare();
    }

    public CloseableIterator<Repo> forAccount(Account account) throws SQLException {
        byAccountArg.setValue(account.getId());
        return iterate(byAccount);
    }

    @Override
    protected EventDaoBase<Repo> createEvent() {
        return new EventDaoRepo();
    }
}
