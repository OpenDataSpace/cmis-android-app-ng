package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.AccountAdapter;
import org.opendataspace.android.operation.OperationAccountUpdate;

public class FragmentAccountList extends FragmentBaseList {

    private AccountAdapter adapter;

    public FragmentAccountList() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new AccountAdapter(OdsApp.get().getViewManager().getAccounts(), getActivity());
        setListAdapter(adapter);
        setEmptyText(getString(R.string.accounts_empty));
    }

    @Override
    public void onDestroyView() {
        adapter.dispose();
        super.onDestroyView();
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
        switch (item.getItemId()) {
        case R.id.menu_account_add:
            getNavigation().openFile(FragmentAccountDetails.class, new OperationAccountUpdate(new Account()));
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    void onListItemClick(int position) {
        getNavigation()
                .openFile(FragmentAccountDetails.class, new OperationAccountUpdate(adapter.getObject(position)));
    }
}
