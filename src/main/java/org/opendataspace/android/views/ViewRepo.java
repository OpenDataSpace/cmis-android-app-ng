package org.opendataspace.android.views;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.event.EventDaoRepo;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.Repo;

import java.sql.SQLException;

public class ViewRepo extends ViewBase<Repo> {

    private long accId;

    public void onEventMainThread(EventDaoRepo event) {
        processEvent(event);
    }

    @Override
    protected CloseableIterator<Repo> iterate(DaoBase<Repo> dao, Account acc) throws SQLException {
        DaoRepo rep = (DaoRepo) dao;
        setAccount(acc);
        return rep.forAccount(acc);
    }

    @Override
    protected boolean isValid(Repo val) {
        return val.getAccountId() == accId;
    }

    public void setAccount(Account acc) {
        accId = acc.getId();
    }
}
