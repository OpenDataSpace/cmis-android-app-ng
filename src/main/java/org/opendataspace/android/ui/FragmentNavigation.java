package org.opendataspace.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterMerge;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.AccountAdapter;

public class FragmentNavigation extends FragmentBase
        implements LoaderManager.LoaderCallbacks<CloseableIterator<Account>> {

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
        accounts = new AccountAdapter(ac, OdsApp.get().getDatabase().getAccounts());

        DataAdapterMerge merge = new DataAdapterMerge();
        merge.addAdapter(accounts);
        merge.addAdapter(new ArrayAdapter<>(ac, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1,
                new String[] {getString(R.string.nav_manage)}));

        spin.setAdapter(merge);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == adapterView.getCount() - 1 && accounts.getCount() != 0) {
                    actionManage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // nothing
            }
        });

        ac.findViewById(R.id.action_nav_settings).setOnClickListener(view -> actionSettings());
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

    private void actionManage() {
        ActivityMain ac = getMainActivity();
        ac.getNavigation().openRootFolder(FragmentAccountList.class, null);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityMain ac = getMainActivity();

        if (ac == null) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
        case R.id.menu_main_about:
            ac.getNavigation().openDialog(FragmentAbout.class, null);
            break;

        case R.id.menu_main_settings:
            actionSettings();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionSettings() {
        getMainActivity().getNavigation().openDialog(FragmentSettings.class, null);
    }
}
