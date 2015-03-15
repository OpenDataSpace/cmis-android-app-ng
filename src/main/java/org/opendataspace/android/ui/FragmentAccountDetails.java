package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.opendataspace.android.app.beta.R;

public class FragmentAccountDetails extends FragmentBase implements ActionMode.Callback {

    private ActionMode mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ActionBarActivity ac = (ActionBarActivity) getActivity();
        mode = ac.startSupportActionMode(this);
        mode.set
    }

    @Override
    public void onStop() {
        if (mode != null) {
            mode.finish();
        }

        super.onStop();
    }

    @Override
    public boolean needDrawer() {
        return false;
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.account_title);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.setTitle(getTile(getActivity()));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mode = null;
    }
}
