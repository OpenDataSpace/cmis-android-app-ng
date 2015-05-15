package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.event.EventDaoRepo;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class ViewRepo extends ViewBase<Repo> {

    private long accId = ObjectBase.INVALID_ID;

    public void onEventMainThread(EventDaoRepo event) {
        processEvent(event);
    }

    @Override
    protected CloseableIterator<Repo> iterate(DaoBase<Repo> dao) throws SQLException {
        DaoRepo rep = (DaoRepo) dao;
        return accId != ObjectBase.INVALID_ID ? rep.forAccount(accId) : null;
    }

    @Override
    protected boolean isValid(Repo val) {
        return val.getAccountId() == accId && val.getType() != Repo.Type.CONFIG;
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoRepo());
    }

    public void setAccount(Account acc) {
        accId = acc != null ? acc.getId() : ObjectBase.INVALID_ID;
    }
}
