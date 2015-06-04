package org.opendataspace.android.data;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.object.Account;

import java.sql.SQLException;

public class DaoAccount extends DaoBaseView<Account> {

    DaoAccount(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, Account.class);
    }

    @Override
    protected EventDaoBase<Account> createEvent() {
        return new EventDaoAccount();
    }
}
