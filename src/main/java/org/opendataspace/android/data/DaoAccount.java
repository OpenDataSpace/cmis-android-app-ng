package org.opendataspace.android.data;

import com.j256.ormlite.support.ConnectionSource;
import org.opendataspace.android.objects.Account;

import java.sql.SQLException;

public class DaoAccount extends DaoBase<Account, Long> {

    DaoAccount(ConnectionSource connectionSource, Class<Account> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
