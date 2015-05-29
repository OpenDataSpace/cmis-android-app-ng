package org.opendataspace.android.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.ObjectBaseId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DataAdapterList<T extends ObjectBaseId> extends DataAdapterBase {

    private final List<T> data = new ArrayList<>();
    private final Set<Long> selected = new HashSet<>();

    protected DataAdapterList(Context context, int resId) {
        super(context, resId);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return getObject(i);
    }

    @Override
    public long getItemId(int i) {
        return getObject(i).getId();
    }

    public T getObject(int i) {
        return data.get(i);
    }

    protected void sort(List<T> data) {
        // nothing
    }

    public void select(int position) {
        long id = getObject(position).getId();

        if (isSelected(id)) {
            selected.remove(id);
        } else {
            selected.add(id);
        }

        notifyDataSetChanged();
    }

    public void clearSelection() {
        if (selected.isEmpty()) {
            return;
        }

        selected.clear();
        notifyDataSetChanged();
    }

    private boolean isSelected(long id) {
        return selected.contains(id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vw = super.getView(position, convertView, parent);
        T item = getObject(position);

        if (isSelected(item.getId())) {
            vw.setBackgroundColor(inf.getContext().getResources().getColor(R.color.selected));
        } else {
            vw.setBackgroundColor(Color.TRANSPARENT);
        }

        updateView(item, vw);
        return vw;
    }

    protected abstract void updateView(T item, View view);

    public List<T> getSelected() {
        List<T> res = new ArrayList<>();

        for (T cur : data) {
            if (isSelected(cur.getId())) {
                res.add(cur);
            }
        }

        return res;
    }

    public void selectAll() {
        for (T cur : data) {
            selected.add(cur.getId());
        }

        notifyDataSetChanged();
    }

    protected void assign(List<T> val) {
        data.clear();
        data.addAll(val);
        sort(data);
    }

    public void append(T val) {
        if (val == null) {
            return;
        }

        data.add(val);
        sort(data);
        notifyDataSetChanged();
    }

    public void remove(List<T> ls) {
        data.removeAll(ls);
        notifyDataSetChanged();
    }
}
