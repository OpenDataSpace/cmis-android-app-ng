package org.opendataspace.android.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.Navigation;

public class ActivityMain extends ActivityBase {

    private final Navigation nav = new Navigation();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                OdsApp.get().getPrefs().isTablet() ? R.layout.activity_main_tablet : R.layout.activity_main_phone);

        nav.initialize(this, savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_main_about:
            nav.openDialog(FragmentAbout.class);
            break;

        case R.id.menu_main_settings:
            nav.openDialog(FragmentSettings.class);
            break;

        case android.R.id.home:
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        nav.save(outState);
    }

    @Override
    public void onBackPressed() {
        if (!nav.backPressed()) {
            super.onBackPressed();
        }
    }
}
