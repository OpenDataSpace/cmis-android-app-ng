package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.opendataspace.android.app.OdsApplication;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavState;
import org.opendataspace.android.navigation.Navigation;

public class ActivityDialog extends ActivityBase {

    public static final String ARG_NAV_STATE = "ods.navstate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        try {
            NavState state = OdsApplication.gson.fromJson(getIntent().getStringExtra(ARG_NAV_STATE), NavState.class);
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.dialog_view_frame, Navigation.createFragment(state));
            ft.commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dialog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_dialog_close:
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
