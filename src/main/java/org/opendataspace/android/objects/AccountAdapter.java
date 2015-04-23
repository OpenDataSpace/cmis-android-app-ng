package org.opendataspace.android.objects;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.views.ViewAdapter;
import org.opendataspace.android.views.ViewBase;

public class AccountAdapter extends ViewAdapter<Account> {

    public AccountAdapter(ViewBase<Account> view, Context context) {
        super(view, context, android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        TextView vw = (TextView) super.getView(position, convertView, parent);
        vw.setText(getObject(position).getName());
        return vw;
    }

    public void onEventMainThread(EventDaoAccount event) {
        invalidate();
    }
}
