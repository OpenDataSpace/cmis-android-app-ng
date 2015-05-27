package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

public class AccountAdapter extends ViewAdapter<Account> {

    public AccountAdapter(ViewBase<Account> view, Context context) {
        super(view, context, R.layout.delegate_list_item2);
    }

    @Override
    public void updateView(Account item, View view) {
        TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        TextView tw2 = (TextView) view.findViewById(R.id.text_listitem_secondary);

        tw1.setText(item.getDisplayName());
        tw2.setText(item.getDescription());
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoAccount event) {
        invalidate();
    }
}
