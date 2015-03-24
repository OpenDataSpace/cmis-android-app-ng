package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationState;

public class ActivityDialog extends ActivityBase {

    public static final String ARG_NAV_STATE = "ods.navstate";
    private static final String TAG_CONTENT = "content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        try {
            NavigationState state =
                    OdsApp.gson.fromJson(getIntent().getStringExtra(ARG_NAV_STATE), NavigationState.class);
            FragmentBase fgm = Navigation.createFragment(state);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.dialog_view_frame, fgm, TAG_CONTENT);
            ft.commitAllowingStateLoss();

            getSupportActionBar().setTitle(fgm.getTile(this));
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentBase fgm = getFragment();

        if (fgm != null && fgm.backPressed()) {
            return;
        }

        super.onBackPressed();
    }

    private FragmentBase getFragment() {
        return (FragmentBase) getSupportFragmentManager().findFragmentByTag(TAG_CONTENT);
    }
}
