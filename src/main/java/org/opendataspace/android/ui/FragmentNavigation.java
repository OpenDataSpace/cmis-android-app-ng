package org.opendataspace.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.account.Account;
import org.opendataspace.android.account.AccountAdapter;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;

import java.sql.SQLException;

public class FragmentNavigation extends Fragment implements LoaderManager.LoaderCallbacks<CloseableIterator<Account>> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<CloseableIterator<Account>> onCreateLoader(int id, Bundle args) {
        try {
            return OdsApp.get().getDatabase().getAccounts().getSQLCursorLoader(getActivity());
        } catch (SQLException ex) {
            Log.w(getClass().getSimpleName(), ex);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<CloseableIterator<Account>> loader, CloseableIterator<Account> data) {
        try {
            Activity ac = getActivity();
            Spinner spin = (Spinner) ac.findViewById(R.id.spin_nav_accounts);

            spin.setAdapter(new AccountAdapter(ac, data, OdsApp.get().getDatabase().getAccounts()));
        } catch (SQLException ex) {
            Log.w(getClass().getSimpleName(), ex);
        }
    }

    @Override
    public void onLoaderReset(Loader<CloseableIterator<Account>> loader) {
        Activity ac = getActivity();

        if (ac == null) {
            return;
        }

        Spinner spin = (Spinner) ac.findViewById(R.id.spin_nav_accounts);

        if (spin == null) {
            return;
        }

        AccountAdapter adp = (AccountAdapter) spin.getAdapter();

        if (adp != null) {
            adp.resetCursor();
        }

        spin.setAdapter(null);
    }
}
