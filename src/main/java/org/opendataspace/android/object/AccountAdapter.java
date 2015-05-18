package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View vw = super.getView(position, convertView, parent);
        TextView tw1 = (TextView) vw.findViewById(R.id.text_listitem_primary);
        TextView tw2 = (TextView) vw.findViewById(R.id.text_listitem_secondary);
        Account acc = getObject(position);

        tw1.setText(acc.getDisplayName());
        tw2.setText(acc.getDescription());
        return vw;
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoAccount event) {
        invalidate();
    }
}
