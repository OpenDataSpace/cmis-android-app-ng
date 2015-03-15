package org.opendataspace.android.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DataAdapterViews extends BaseAdapter {

    private final List<View> list = new ArrayList<>();

    public DataAdapterViews(View... views) {
        Collections.addAll(list, views);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return list.get(i);
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return list.get(i);
    }
}
