package org.opendataspace.android.views;

import android.content.Context;

import de.greenrobot.event.EventBus;
import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.data.DataAdapter;
import org.opendataspace.android.objects.ObjectBase;

import java.util.List;

public class ViewAdapter<T extends ObjectBase> extends DataAdapter implements CompatDisposable {

    private final List<T> data;

    public ViewAdapter(ViewBase<T> view, Context context, int resId) {
        super(context, resId);
        this.data = view.getObjects();
        EventBus.getDefault().register(this);
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

    protected T getObject(int i) {
        return data.get(i);
    }

    public void onEvent(ViewEvent<T> event) {
        invalidate();
    }

    @Override
    public void dispose() {
        EventBus.getDefault().unregister(this);
    }
}