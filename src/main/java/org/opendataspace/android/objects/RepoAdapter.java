package org.opendataspace.android.objects;

import android.content.Context;

import org.opendataspace.android.views.ViewAdapter;
import org.opendataspace.android.views.ViewBase;
import org.opendataspace.android.views.ViewRepoEvent;

public class RepoAdapter extends ViewAdapter<Repo> {

    public RepoAdapter(ViewBase<Repo> view, Context context, int resId) {
        super(view, context, resId);
    }

    public void onEvent(ViewRepoEvent event) {
        invalidate();
    }
}
