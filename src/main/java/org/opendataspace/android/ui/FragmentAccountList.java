package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.AccountAdapter;
import org.opendataspace.android.operations.OperationAccount;

public class FragmentAccountList extends FragmentBaseList
        implements LoaderManager.LoaderCallbacks<CloseableIterator<Account>> {

    private AccountAdapter accounts;
    private boolean isEmpty = true;

    public FragmentAccountList() {
        try {
            isEmpty = OdsApp.get().getDatabase().getAccounts().countOf() == 0;
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accounts = new AccountAdapter(getActivity(), OdsApp.get().getDatabase().getAccounts());
        setListAdapter(accounts);
        setEmptyText(getString(R.string.accounts_empty));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<CloseableIterator<Account>> onCreateLoader(int id, Bundle args) {
        return OdsApp.get().getDatabase().getAccounts().getLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<CloseableIterator<Account>> loader, CloseableIterator<Account> data) {
        accounts.swapResults(data);
        isEmpty = accounts.getCount() == 0;
    }

    @Override
    public void onLoaderReset(Loader<CloseableIterator<Account>> loader) {
        accounts.swapResults(null);
        isEmpty = true;
    }

    @Override
    public boolean backPressed() {
        if (isEmpty) {
            getActivity().finish();
            return true;
        }

        return false;
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.accounts_title);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_accounts;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityMain ac = getMainActivity();

        switch (item.getItemId()) {
        case R.id.menu_accounts_add:
            ac.getNavigation().openFile(FragmentAccountDetails.class, new OperationAccount(new Account()));
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    void onListItemClick(int position) {
        getMainActivity().getNavigation()
                .openFile(FragmentAccountDetails.class, new OperationAccount((Account) accounts.getItem(position)));
    }
}
