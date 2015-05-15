package org.opendataspace.android.view;

import android.content.Context;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.CompatEvent;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataAdapter;
import org.opendataspace.android.object.ObjectBase;

import java.util.ArrayList;
import java.util.List;

public class ViewAdapter<T extends ObjectBase> extends DataAdapter implements CompatDisposable {

    private final List<T> data = new ArrayList<>();
    private final ViewBase<T> view;

    public ViewAdapter(ViewBase<T> view, Context context, int resId) {
        super(context, resId);
        this.view = view;
        data.addAll(view.getObjects());
        OdsApp.bus.register(this, CompatEvent.PRIORITY_ADAPTER);
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

    public int getPosition(T val) {
        return data.indexOf(val);
    }

    @Override
    public void invalidate() {
        data.clear();
        data.addAll(view.getObjects());
        super.invalidate();
    }
}
