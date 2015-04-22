package org.opendataspace.android.views;

import com.j256.ormlite.dao.CloseableIterator;
import de.greenrobot.event.EventBus;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoRepoEvent;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.Repo;

import java.sql.SQLException;

public class ViewRepo extends ViewBase<Repo> {

    public void onEventMainThread(DaoRepoEvent event) {
        if (processEvent(event)) {
            EventBus.getDefault().post(new ViewRepoEvent());
        }
    }

    @Override
    protected CloseableIterator<Repo> iterate(DaoBase<Repo> dao, Account acc) throws SQLException {
        DaoRepo rep = (DaoRepo) dao;
        return rep.forAccount(acc);
    }
}
