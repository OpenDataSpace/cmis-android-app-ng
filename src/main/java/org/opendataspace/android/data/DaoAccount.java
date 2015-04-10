package org.opendataspace.android.data;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import org.opendataspace.android.objects.Account;

import java.sql.SQLException;

public class DaoAccount extends DaoBase<Account> {

    DaoAccount(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, Account.class);
    }
}
