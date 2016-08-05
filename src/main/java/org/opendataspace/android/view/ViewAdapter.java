package org.opendataspace.android.view;

import android.content.Context;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataAdapterList;
import org.opendataspace.android.object.ObjectBase;

public abstract class ViewAdapter<T extends ObjectBase> extends DataAdapterList<T> implements CompatDisposable {

    private final ViewBase<T> view;

    protected ViewAdapter(final ViewBase<T> view, final Context context, final int resId) {
        super(context, resId);
        this.view = view;
        assign(view.getObjects());
        OdsApp.bus.register(this);
    }

    @Override
    public void dispose() {
        OdsApp.bus.unregister(this);
    }

    @Override
    public void invalidate() {
        assign(view.getObjects());
        super.invalidate();
    }

    protected boolean isValid(final T value) {
        return view.isValid(value);
    }
}
