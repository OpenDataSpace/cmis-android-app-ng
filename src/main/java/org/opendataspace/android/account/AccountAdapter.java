package org.opendataspace.android.account;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DataAdapter;

public class AccountAdapter extends DataAdapter<Account> {

    public AccountAdapter(Context context, CloseableIterator<Account> data, DaoBase<Account, ?> dao) {
        super(context, data, dao, android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void bindView(Context context, View view, Account item) {
        TextView tv = (TextView) view;
        tv.setText(item.getName());
    }
}
