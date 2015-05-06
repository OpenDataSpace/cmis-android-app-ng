package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventAccountConfig;
import org.opendataspace.android.operation.OperationAccountConfig;
import org.opendataspace.android.storage.Storage;

@SuppressLint("Registered")
public class ActivityBase extends ActionBarActivity {

    private Toast toast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        OdsApp.bus.register(this);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        updateBranding();

        getSupportActionBar().setElevation(getResources().getDimensionPixelSize(R.dimen.pad) / 2);
        setRequestedOrientation(OdsApp.get().getPrefs().isTablet() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.remove("android:support:fragments"); // do not store fragment state
    }

    void showToast(final int messageCode) {
        showToast(getString(messageCode));
    }

    void showToast(final String message) {
        toast.setText(message);
        toast.show();
    }

    @Override
    public void onDestroy() {
        OdsApp.bus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(EventAccountConfig val) {
        updateBranding();
    }

    private void updateBranding() {
        try {
            Drawable d = Storage.getBrandingDrawable(this, OperationAccountConfig.BRAND_ICON,
                    OdsApp.get().getViewManager().getCurrentAccount());

            if (d != null) {
                getSupportActionBar().setIcon(d);
            } else {
                getSupportActionBar().setIcon(R.drawable.ic_logo_small);
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }
}
