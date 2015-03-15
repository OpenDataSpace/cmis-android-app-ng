package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavState;
import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationCallback;

public class ActivityDialog extends ActivityBase {

    public static final String ARG_NAV_STATE = "ods.navstate";
    public static final String TAG_CONTENT = "content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        try {
            NavState state = OdsApp.gson.fromJson(getIntent().getStringExtra(ARG_NAV_STATE), NavState.class);
            Fragment fgm = Navigation.createFragment(state);

            if (fgm instanceof NavigationCallback) {
                getSupportActionBar().setTitle(((NavigationCallback) fgm).getTile(this));
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.dialog_view_frame, fgm, TAG_CONTENT);
            ft.commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fgm = getFragment();

        if (fgm != null && fgm instanceof NavigationCallback && ((NavigationCallback) fgm).backPressed()) {
            return;
        }

        super.onBackPressed();
    }

    private Fragment getFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_CONTENT);
    }
}
