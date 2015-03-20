package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;

@SuppressLint("Registered")
class ActivityBase extends ActionBarActivity {

    private Toast toast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_logo);

        getSupportActionBar().setElevation(getResources().getDimensionPixelSize(R.dimen.pad) / 2);
        setRequestedOrientation(OdsApp.get().getPrefs().isTablet() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.remove("android:support:fragments"); // do not store fragment state
    }

    public void showToast(final int messageCode) {
        showToast(getString(messageCode));
    }

    public void showToast(final String message) {
        toast.setText(message);
        toast.show();
    }
}
