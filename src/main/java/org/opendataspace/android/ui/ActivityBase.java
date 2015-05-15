package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventAccountConfig;
import org.opendataspace.android.operation.OperationAccountConfig;
import org.opendataspace.android.storage.Storage;

@SuppressLint("Registered")
public class ActivityBase extends AppCompatActivity {

    private Toast toast;
    private ProgressDialog waitDialog;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        OdsApp.bus.register(this);

        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setElevation(getResources().getDimensionPixelSize(R.dimen.pad) / 2);
        }

        updateBranding();
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
            ActionBar bar = getSupportActionBar();

            if (bar == null) {
                return;
            }

            Drawable d = Storage.getBrandingDrawable(this, OperationAccountConfig.BRAND_ICON,
                    OdsApp.get().getViewManager().getCurrentAccount());

            if (d != null) {
                bar.setIcon(d);
            } else {
                bar.setIcon(R.drawable.ic_logo_small);
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    public void startWaitDialog(String title, String message, DialogInterface.OnCancelListener callback) {
        if (!isWaiting()) {
            waitDialog = ProgressDialog.show(this, title, message, true, callback != null, callback);
        }
    }

    public void stopWait() {
        if (isWaiting()) {
            waitDialog.dismiss();
            waitDialog = null;
        }
    }

    public boolean isWaiting() {
        return waitDialog != null;
    }
}
