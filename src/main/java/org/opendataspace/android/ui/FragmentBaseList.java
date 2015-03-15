package org.opendataspace.android.ui;

import android.content.Context;
import android.support.v4.app.ListFragment;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavigationCallback;

public class FragmentBaseList extends ListFragment implements NavigationCallback {

    @Override
    public boolean needDrawer() {
        return true;
    }

    @Override
    public boolean backPressed() {
        return false;
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.app_name);
    }
}
