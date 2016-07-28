package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationState;

public class ActivityDialog extends ActivityBase {

    public static final String ARG_NAV_STATE = "ods.navstate";
    private static final String TAG_CONTENT = "content";

    @Override
    protected void onInit(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_dialog);
        setSupportActionBar((Toolbar) findViewById(R.id.dialog_view_toolbar));

        NavigationState state = OdsApp.gson.fromJson(getIntent().getStringExtra(ARG_NAV_STATE), NavigationState.class);
        FragmentBase fgm = Navigation.createFragment(state);

        if (fgm == null) {
            throw new IllegalArgumentException();
        }

        applyFragmment(fgm);
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

    @Override
    protected void onStart() {
        super.onStart();
        OdsApp.get().getNavigation().setDialog(this);
    }

    @Override
    protected void onStop() {
        OdsApp.get().getNavigation().setDialog(null);
        super.onStop();
    }

    public void applyFragmment(FragmentBase fgm) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.dialog_view_frame, fgm, TAG_CONTENT);
        ft.commitAllowingStateLoss();

        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setTitle(fgm.getTile(this));
        }
    }
}
