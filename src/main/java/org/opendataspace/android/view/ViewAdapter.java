package org.opendataspace.android.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapter;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.object.ObjectBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ViewAdapter<T extends ObjectBase> extends DataAdapter implements CompatDisposable {

    private final List<T> data = new ArrayList<>();
    private final ViewBase<T> view;
    private final Set<Long> selected = new HashSet<>();

    protected ViewAdapter(ViewBase<T> view, Context context, int resId) {
        super(context, resId);
        this.view = view;
        data.addAll(view.getObjects());
        sort(data);
        OdsApp.bus.register(this, Event.PRIORITY_ADAPTER);
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

    @Override
    public void dispose() {
        OdsApp.bus.unregister(this);
    }

    @Override
    public void invalidate() {
        data.clear();
        data.addAll(view.getObjects());
        sort(data);
        super.invalidate();
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

        super.invalidate();
    }

    public void clearSelection() {
        if (selected.isEmpty()) {
            return;
        }

        selected.clear();
        super.invalidate();
    }

    protected boolean isSelected(long id) {
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

    public abstract void updateView(T item, View view);

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

        super.invalidate();
    }
}
