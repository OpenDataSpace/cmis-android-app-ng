package org.opendataspace.android.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import org.opendataspace.android.app.OdsApplication;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.ui.ActivityDialog;
import org.opendataspace.android.ui.FragmentNavigation;

import java.util.Collections;
import java.util.Stack;

public class Navigation {

    private static final String NAV_TAG = "nav";
    private static final String MAIN_TAG = "main";
    private static final String DETAILS_TAG = "details";
    private static final String ARG_BACKSTACK = "ods.backstack";

    private FragmentManager fm;
    private ActionBar bar;
    private DrawerLayout drawer;
    private Context context;
    private final Stack<NavState> backstack = new Stack<>();

    public void initialize(ActionBarActivity activity, Bundle state) {
        fm = activity.getSupportFragmentManager();
        bar = activity.getSupportActionBar();
        drawer = (DrawerLayout) activity.findViewById(R.id.main_view_root);
        context = activity;
        backstack.clear();

        if (state != null) {
            NavState[] ns = OdsApplication.gson.fromJson(state.getString(ARG_BACKSTACK), NavState[].class);

            if (ns != null) {
                Collections.addAll(backstack, ns);
            }
        }

        if (backstack.isEmpty()) {
            backstack.add(new NavState(NavScope.MAIN, FragmentNavigation.class));
        }

        applyFragment(R.id.main_view_drawer, new FragmentNavigation(), NAV_TAG);
        navigate(backstack.lastElement());
    }

    private void applyFragment(int frameId, Fragment f, String tag) {
        try {
            final FragmentTransaction ft = fm.beginTransaction();
            ft.replace(frameId, f, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }
    }

    private void navigate(NavState ns) {
        Fragment fgm = createFragment(ns);

        if (fgm == null) {
            return;
        }

        boolean canGoBack = backstack.size() > 1;
        bar.setDisplayHomeAsUpEnabled(canGoBack);
        drawer.setDrawerLockMode(canGoBack ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (OdsApplication.get().getPrefs().isTablet()) {
            switch (ns.getNavigationScope()) {
            case DETAILS:
                applyFragment(R.id.main_view_details, fgm, DETAILS_TAG);
                return;
            case DIALOG:
                Intent intent = new Intent(context, ActivityDialog.class);
                intent.putExtra(ActivityDialog.ARG_NAV_STATE, OdsApplication.gson.toJson(ns, NavState.class));
                context.startActivity(intent);
                return;
            }
        }

        applyFragment(R.id.main_view_frame, fgm, MAIN_TAG);
    }

    public static Fragment createFragment(NavState ns) {
        try {
            return ns.getFragmentClass().newInstance();
        } catch (Exception ex) {
            Log.w(Navigation.class.getSimpleName(), ex);
        }

        return null;
    }

    public void save(Bundle state) {
        state.putString(ARG_BACKSTACK, OdsApplication.gson.toJson(backstack));
    }

    public void openDialog(Class<? extends Fragment> cls) {
        NavState ns = new NavState(NavScope.DIALOG, cls);
        addBackstack(ns);
        navigate(ns);
    }

    private void addBackstack(NavState ns) {
        if (!OdsApplication.get().getPrefs().isTablet() || ns.getNavigationScope() == NavScope.MAIN) {
            backstack.add(ns);
        }
    }

    public boolean backPressed() {
        if (backstack.size() < 2) {
            return false;
        }

        backstack.pop();
        navigate(backstack.lastElement());
        return true;
    }
}
