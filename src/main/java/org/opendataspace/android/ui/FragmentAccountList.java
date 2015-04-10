package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.AccountAdapter;
import org.opendataspace.android.operations.OperationAccount;

public class FragmentAccountList extends FragmentBaseList {

    private AccountAdapter accounts;

    public FragmentAccountList() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accounts = new AccountAdapter(OdsApp.get().getViewManager().getAccounts(), getActivity());
        setListAdapter(accounts);
        setEmptyText(getString(R.string.accounts_empty));
    }

    @Override
    public void onDestroyView() {
        accounts.dispose();
        super.onDestroyView();
    }

    @Override
    public boolean backPressed() {
        if (accounts.getCount() == 0) {
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
