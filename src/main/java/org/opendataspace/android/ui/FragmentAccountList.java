package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.AccountAdapter;
import org.opendataspace.android.operation.OperationAccountUpdate;

import java.sql.SQLException;

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
        try {
            if (OdsApp.get().getDatabase().getAccounts().countOf() == 0) {
                getActivity().finish();
                return true;
            }
        } catch (SQLException ex) {
            OdsLog.ex(getClass(), ex);
        }

        return false;
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.accounts_title);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_account_list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityMain ac = getMainActivity();

        switch (item.getItemId()) {
        case R.id.menu_account_add:
            ac.getNavigation().openFile(FragmentAccountDetails.class, new OperationAccountUpdate(new Account()));
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    void onListItemClick(int position) {
        getMainActivity().getNavigation()
                .openFile(FragmentAccountDetails.class, new OperationAccountUpdate((Account) accounts.getItem(position)));
    }
}
