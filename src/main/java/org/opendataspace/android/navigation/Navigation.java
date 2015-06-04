package org.opendataspace.android.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import org.opendataspace.android.app.CompatKeyboard;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.ui.*;

import java.util.Collections;
import java.util.ListIterator;
import java.util.Stack;

public class Navigation implements NavigationInterface {

    private static final String TAG_NAVGATION = "nav";
    private static final String TAG_MAIN = "main";
    private static final String TAG_DETAILS = "details";
    private static final String ARG_BACKSTACK = "ods.backstack";

    private final FragmentManager fm;
    private final ActionBar bar;
    private final DrawerLayout drawer;
    private final Activity context;
    private final Stack<NavigationState> backstack = new Stack<>();
    private final boolean isTablet;
    private final ActionBarDrawerToggle toggle;
    private ActivityDialog dialog;

    public Navigation(ActivityMain activity, Bundle state) {
        fm = activity.getSupportFragmentManager();
        bar = activity.getSupportActionBar();
        drawer = (DrawerLayout) activity.findViewById(R.id.main_view_root);
        context = activity;
        isTablet = OdsApp.get().getPrefs().isTablet();
        toggle = new ActionBarDrawerToggle(activity, drawer, R.string.common_opendrawer, R.string.common_closedrawer);

        backstack.clear();
        drawer.setDrawerShadow(null, 0);
        drawer.setScrimColor(activity.getResources().getColor(android.R.color.transparent));

        if (state != null) {
            NavigationState[] ns = OdsApp.gson.fromJson(state.getString(ARG_BACKSTACK), NavigationState[].class);

            if (ns != null) {
                Collections.addAll(backstack, ns);
            }
        }

        FragmentNavigation nav = new FragmentNavigation();
        nav.setNonMain();
        applyFragment(R.id.main_view_drawer, nav, TAG_NAVGATION);
        initialize();
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
            OdsLog.ex(getClass(), ex);
        }
    }

    private void navigate(NavigationState ns) {
        if (isTablet && ns.getNavigationScope() == NavigationScope.DIALOG && dialog == null) {
            Intent intent = new Intent(context, ActivityDialog.class);
            intent.putExtra(ActivityDialog.ARG_NAV_STATE, OdsApp.gson.toJson(ns, NavigationState.class));
            context.startActivity(intent);
            return;
        }

        FragmentBase fgm = createFragment(ns);

        if (fgm == null) {
            return;
        }

        boolean needDrawer = backstack.size() > 1 && fgm.needDrawer();

        if (isTablet) {
            switch (ns.getNavigationScope()) {
            case DETAILS:
                applyFragment(R.id.main_view_details, fgm, TAG_DETAILS);
                break;

            case MAIN:
                applyFragment(R.id.main_view_frame, fgm, TAG_MAIN);
                break;

            case DIALOG:
                dialog.applyFragmment(fgm);
                return;
            }
        } else {
            applyFragment(R.id.main_view_frame, fgm, TAG_MAIN);
        }

        updateMenu();
        bar.setTitle(fgm.getTile(context));
        bar.setDisplayHomeAsUpEnabled(needDrawer);
        toggle.syncState();
        drawer.setDrawerLockMode(needDrawer ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.START);
    }

    private void closeDrawer() {
        drawer.closeDrawer(Gravity.START);
    }

    public static FragmentBase createFragment(NavigationState ns) {
        try {
            Class<?> cls = ns.getFragmentClass();
            OperationBase op = ns.getOperation();
            return (FragmentBase) (op != null ? cls.getConstructor(op.getClass()).newInstance(op) : cls.newInstance());
        } catch (Exception ex) {
            OdsLog.ex(Navigation.class, ex);
        }

        return null;
    }

    @Override
    public void save(Bundle state) {
        state.putString(ARG_BACKSTACK, OdsApp.gson.toJson(backstack));
    }

    @Override
    public void openDialog(Class<? extends FragmentBase> cls, OperationBase op) {
        navigate(cls, op, NavigationScope.DIALOG, false);
    }

    private void addBackstack(NavigationState ns) {
        if (isTablet && ns.getNavigationScope() == NavigationScope.DIALOG) {
            return;
        }

        if (!backstack.isEmpty() && backstack.lastElement().getNavigationScope() == NavigationScope.DIALOG) {
            backstack.pop();
        }

        backstack.add(ns);
    }

    @Override
    public boolean backPressed() {
        if (dialog != null) {
            dialog.onBackPressed();
            return true;
        }

        if (backstack.size() < 2) {
            return false;
        }

        FragmentBase fgm = getTopFragment();

        if (fgm != null && fgm.backPressed()) {
            return true;
        }

        CompatKeyboard.hide(context);
        NavigationState state = backstack.pop();

        if (isTablet && state.getNavigationScope() == NavigationScope.DETAILS) {
            applyFragment(R.id.main_view_details, null, TAG_DETAILS);
        } else {
            try {
                navigate(backstack.lastElement());
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return true;
    }

    @Override
    public FragmentBase getTopFragment() {
        return (FragmentBase) fm.findFragmentByTag(
                (isTablet && backstack.lastElement().getNavigationScope() == NavigationScope.DETAILS) ? TAG_DETAILS :
                        TAG_MAIN);
    }

    private void goHome() {
        if (backstack.size() < 2) {
            return;
        }

        CompatKeyboard.hide(context);
        backstack.setSize(1);

        if (isTablet) {
            applyFragment(R.id.main_view_details, null, TAG_DETAILS);
        }
    }

    @Override
    public void openRootFolder(Class<? extends FragmentBase> cls, OperationBase op) {
        navigate(cls, op, NavigationScope.MAIN, true);
    }

    @Override
    public void openDrawer() {
        drawer.openDrawer(Gravity.START);
    }

    @Override
    public void updateTitle() {
        FragmentBase fgm = getTopFragment();

        if (fgm != null) {
            bar.setTitle(fgm.getTile(context));
        }
    }

    @Override
    public void updateMenu() {
        ActivityCompat.invalidateOptionsMenu(context);
    }

    @Override
    public void openFile(Class<? extends FragmentBase> cls, OperationBase op) {
        navigate(cls, op, NavigationScope.DETAILS, false);
    }

    private void navigate(Class<? extends FragmentBase> cls, OperationBase op, NavigationScope scope,
                          boolean needHome) {
        closeDrawer();
        CompatKeyboard.hide(context);
        FragmentBase top = getTopFragment();

        if (top != null && top.getClass().equals(cls) && backstack.lastElement().getNavigationScope() == scope) {
            top.navigationRequest(op);
            return;
        }

        if (needHome) {
            goHome();
        }

        NavigationState ns = new NavigationState(scope, cls, op);
        addBackstack(ns);

        try {
            navigate(ns);
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    @Override
    public void setDialog(ActivityDialog dialog) {
        this.dialog = dialog;
    }

    private void initialize() {
        if (backstack.isEmpty()) {
            backstack.add(new NavigationState(NavigationScope.MAIN, FragmentNavigation.class, null));

            try {
                if (OdsApp.get().getDatabase().getAccounts().countOf() == 0) {
                    OperationAccountUpdate op = new OperationAccountUpdate(new Account());
                    op.setIsFirstAccount();
                    backstack.add(new NavigationState(NavigationScope.DETAILS, FragmentAccountDetails.class, op));
                }
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        ListIterator<NavigationState> it = backstack.listIterator(backstack.size());

        while (it.hasPrevious()) {
            NavigationState cur = it.previous();
            try {
                navigate(cur);
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }

            if (!isTablet || cur.getNavigationScope() == NavigationScope.MAIN) {
                break;
            }
        }
    }
}
