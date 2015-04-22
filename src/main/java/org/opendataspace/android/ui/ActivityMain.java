package org.opendataspace.android.ui;

import android.os.Bundle;
import android.view.MenuItem;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationInterface;

public class ActivityMain extends ActivityBase {

    private Navigation nav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                OdsApp.get().getPrefs().isTablet() ? R.layout.activity_main_tablet : R.layout.activity_main_phone);
        nav = createNavigation(savedInstanceState);
    }

    protected Navigation createNavigation(Bundle savedInstanceState) {
        return new Navigation(this, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            nav.openDrawer();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (nav != null) {
            nav.save(outState);
        }
    }

    @Override
    public void onBackPressed() {
        if (!nav.backPressed()) {
            super.onBackPressed();
        }
    }

    public NavigationInterface getNavigation() {
        return nav;
    }

    public void onDestroy() {
        nav = null;
        super.onDestroy();
    }
}
