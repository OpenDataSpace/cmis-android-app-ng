package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.event.EventDaoRepo;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class ViewRepo extends ViewBase<Repo> {

    private long accId = -1;

    public void onEventMainThread(EventDaoRepo event) {
        processEvent(event);
    }

    @Override
    protected CloseableIterator<Repo> iterate(DaoBase<Repo> dao, Account acc) throws SQLException {
        DaoRepo rep = (DaoRepo) dao;
        setAccount(acc);
        return acc != null ? rep.forAccount(acc) : null;
    }

    @Override
    protected boolean isValid(Repo val) {
        return val.getAccountId() == accId;
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoRepo());
    }

    public void setAccount(Account acc) {
        accId = acc != null ? acc.getId() : -1;
    }
}
