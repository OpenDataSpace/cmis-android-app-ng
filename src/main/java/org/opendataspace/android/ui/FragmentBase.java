package org.opendataspace.android.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import org.opendataspace.android.app.beta.R;

public class FragmentBase extends Fragment {

    public FragmentBase() {
        setHasOptionsMenu(true);
    }

    public boolean backPressed() {
        return false;
    }

    public String getTile(Context context) {
        return context.getString(R.string.app_name);
    }

    ActivityMain getMainActivity() {
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

    int getMenuResource() {
        return 0;
    }
}
