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
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.ui.ActivityDialog;
import org.opendataspace.android.ui.FragmentAccountList;
import org.opendataspace.android.ui.FragmentNavigation;

import java.util.Collections;
import java.util.Stack;

public class Navigation {

    private static final String TAG_NAVGATION = "nav";
    private static final String TAG_MAIN = "main";
    private static final String TAG_DETAILS = "details";
    private static final String ARG_BACKSTACK = "ods.backstack";

    private FragmentManager fm;
    private ActionBar bar;
    private DrawerLayout drawer;
    private Context context;
    private final Stack<NavState> backstack = new Stack<>();
    private boolean isTablet;
    private ActionBarDrawerToggle toggle;

    public void initialize(ActionBarActivity activity, Bundle state) {
        fm = activity.getSupportFragmentManager();
        bar = activity.getSupportActionBar();
        drawer = (DrawerLayout) activity.findViewById(R.id.main_view_root);
        context = activity;
        isTablet = OdsApp.get().getPrefs().isTablet();
        toggle = new ActionBarDrawerToggle(activity, drawer, R.string.app_opendrawer, R.string.app_closedrawer);

        backstack.clear();
        drawer.setDrawerShadow(null, 0);
        drawer.setScrimColor(activity.getResources().getColor(android.R.color.transparent));

        if (state != null) {
            NavState[] ns = OdsApp.gson.fromJson(state.getString(ARG_BACKSTACK), NavState[].class);

            if (ns != null) {
                Collections.addAll(backstack, ns);
            }
        }

        if (backstack.isEmpty()) {
            backstack.add(new NavState(NavScope.MAIN, FragmentNavigation.class));
        }

        try {
            if (OdsApp.get().getDatabase().getAccounts().countOf() == 0) {
                backstack.add(new NavState(NavScope.MAIN, FragmentAccountList.class));
            }
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }

        FragmentNavigation nav = new FragmentNavigation();
        nav.setHasOptionsMenu(false);
        applyFragment(R.id.main_view_drawer, nav, TAG_NAVGATION);
        navigate(backstack.lastElement());
    }

    private void applyFragment(int frameId, Fragment f, String tag) {
        try {
            final FragmentTransaction ft = fm.beginTransaction();

            if (f != null) {
                ft.replace(frameId, f, tag);
            } else {
                f = fm.findFragmentByTag(tag);

                if (f != null) {
                    ft.remove(f);
                }
            }

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


        boolean needDrawer = backstack.size() > 1;
        boolean needSync = true;

        if (fgm instanceof NavigationCallback) {
            NavigationCallback nc = (NavigationCallback) fgm;

            bar.setTitle(nc.getTile(context));
        }

        if (isTablet) {
            switch (ns.getNavigationScope()) {
            case DETAILS:
                applyFragment(R.id.main_view_details, fgm, TAG_DETAILS);
                break;

            case MAIN:
                applyFragment(R.id.main_view_frame, fgm, TAG_MAIN);
                break;

            case DIALOG:
                Intent intent = new Intent(context, ActivityDialog.class);
                intent.putExtra(ActivityDialog.ARG_NAV_STATE, OdsApp.gson.toJson(ns, NavState.class));
                context.startActivity(intent);
                return;
            }
        } else {
            applyFragment(R.id.main_view_frame, fgm, TAG_MAIN);
        }


        bar.invalidateOptionsMenu();
        bar.setDisplayHomeAsUpEnabled(needDrawer);
        toggle.syncState();
        drawer.setDrawerLockMode(needDrawer ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.LEFT);
    }

    private void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
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
        state.putString(ARG_BACKSTACK, OdsApp.gson.toJson(backstack));
    }

    public void openDialog(Class<? extends Fragment> cls) {
        navigate(cls, NavScope.DIALOG, false);
    }

    private void addBackstack(NavState ns) {
        if (!isTablet || ns.getNavigationScope() != NavScope.DIALOG) {
            backstack.add(ns);
        }
    }

    public boolean backPressed() {
        if (backstack.size() < 2) {
            return false;
        }

        Fragment fgm = getTopFragment();

        if (fgm != null && fgm instanceof NavigationCallback && ((NavigationCallback) fgm).backPressed()) {
            return true;
        }

        NavState state = backstack.pop();

        if (isTablet && state.getNavigationScope() == NavScope.DETAILS) {
            applyFragment(R.id.main_view_details, null, TAG_DETAILS);
        } else {
            navigate(backstack.lastElement());
        }

        return true;
    }

    public Fragment getTopFragment() {
        return fm.findFragmentByTag(
                (isTablet && backstack.lastElement().getNavigationScope() == NavScope.DETAILS) ? TAG_DETAILS :
                        TAG_MAIN);
    }

    private void goHome() {
        if (backstack.size() < 2) {
            return;
        }

        backstack.setSize(1);

        if (isTablet) {
            applyFragment(R.id.main_view_details, null, TAG_DETAILS);
        }
    }

    public void openRootFolder(Class<? extends Fragment> cls) {
        navigate(cls, NavScope.MAIN, true);
    }

    public void openDrawer() {
        drawer.openDrawer(Gravity.START);
    }

    public void openFile(Class<? extends Fragment> cls) {
        navigate(cls, NavScope.DETAILS, false);
    }

    private void navigate(Class<? extends Fragment> cls, NavScope scope, boolean needHome) {
        closeDrawer();

        if (getTopFragment().getClass().equals(cls)) {
            return;
        }

        if (needHome) {
            goHome();
        }

        NavState ns = new NavState(scope, cls);
        addBackstack(ns);
        navigate(ns);
    }
}
