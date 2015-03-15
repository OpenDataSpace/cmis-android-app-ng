package org.opendataspace.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.account.Account;
import org.opendataspace.android.account.AccountAdapter;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterMerge;

public class FragmentNavigation extends Fragment implements LoaderManager.LoaderCallbacks<CloseableIterator<Account>> {

    private AccountAdapter accounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity ac = getActivity();
        Spinner spin = (Spinner) ac.findViewById(R.id.spin_nav_accounts);
        accounts = new AccountAdapter(ac, null, OdsApp.get().getDatabase().getAccounts());

        DataAdapterMerge merge = new DataAdapterMerge();
        merge.addAdapter(accounts);
        merge.addAdapter(new ArrayAdapter<String>(ac, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1,
                new String[]{getString(R.string.text_nav_manage)}));

        spin.setAdapter(merge);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<CloseableIterator<Account>> onCreateLoader(int id, Bundle args) {
        return OdsApp.get().getDatabase().getAccounts().getLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<CloseableIterator<Account>> loader, CloseableIterator<Account> data) {
        accounts.swapResults(data);
    }

    @Override
    public void onLoaderReset(Loader<CloseableIterator<Account>> loader) {
        accounts.swapResults(null);
    }
}
