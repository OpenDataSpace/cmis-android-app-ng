package org.opendataspace.android.objects;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DataAdapter;

public class AccountAdapter extends DataAdapter<Account> {

    public AccountAdapter(Context context, DaoBase<Account, ?> dao) {
        super(context, null, dao, android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void bindView(Context context, View view, Account item) {
        TextView tv = (TextView) view;
        tv.setText(item.getName());
    }
}
