package org.opendataspace.android.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class DataAdapter extends BaseAdapter {

    protected final LayoutInflater inf;
    protected final Context context;
    private final int resId;

    protected DataAdapter(final Context context, final int resId) {
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resId = resId;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(resId, parent, false);
        }

        return convertView;
    }

    protected void invalidate() {
        notifyDataSetChanged();
    }
}
