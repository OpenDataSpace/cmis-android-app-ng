package org.opendataspace.android.account;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DataAdapter;

import java.sql.SQLException;

public class AccountAdapter extends DataAdapter<Account> {

    public AccountAdapter(Context context, CloseableIterator<Account> data, DaoBase<Account, ?> dao) throws
            SQLException {
        super(context, (AndroidDatabaseResults) data.getRawResults(), dao);
    }

    @Override
    public void bindView(View itemView, Context context, Account item) {
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }
}
