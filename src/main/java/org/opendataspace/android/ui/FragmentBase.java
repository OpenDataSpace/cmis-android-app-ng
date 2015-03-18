package org.opendataspace.android.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavigationCallback;

public class FragmentBase extends Fragment implements NavigationCallback {

    public FragmentBase() {
        setHasOptionsMenu(true);
    }

    @Override
    public boolean backPressed() {
        return false;
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.app_name);
    }

    protected ActivityMain getMainActivity() {
        Activity ac = getActivity();
        return ac instanceof ActivityMain ? (ActivityMain) ac : null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ActivityMain ac = getMainActivity();

        if (ac != null && ac.getNavigation().getTopFragment() != this) {
            return;
        }

        int res = getMenuResource();

        if (res != 0) {
            inflater.inflate(res, menu);
        }
    }

    protected int getMenuResource() {
        return 0;
    }
}
