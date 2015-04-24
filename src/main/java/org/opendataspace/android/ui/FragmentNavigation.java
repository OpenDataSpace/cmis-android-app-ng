package org.opendataspace.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.opendataspace.android.app.CompatEvent;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterMerge;
import org.opendataspace.android.event.EventSelectAccount;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.AccountAdapter;
import org.opendataspace.android.object.Action;
import org.opendataspace.android.object.ActionAdapter;
import org.opendataspace.android.object.RepoAdapter;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.view.ViewManager;

public class FragmentNavigation extends FragmentBase {

    private AccountAdapter accounts;
    private RepoAdapter repos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity ac = getActivity();
        ViewManager vm = OdsApp.get().getViewManager();
        ListView lva = widget(R.id.list_nav_accounts);
        ListView lvf = widget(R.id.list_nav_folders);

        DataAdapterMerge adpa = new DataAdapterMerge();
        adpa.addAdapter(accounts = new AccountAdapter(vm.getAccounts(), ac));
        adpa.addAdapter(new ActionAdapter(ac, Action.listOf(ac, R.id.action_nav_addaccount, R.id.action_nav_manage)));
        lva.setAdapter(adpa);
        lva.setOnItemClickListener((adapterView, view1, i, l) -> selectAccount(adapterView, i));

        DataAdapterMerge adpf = new DataAdapterMerge();
        adpf.addAdapter(repos = new RepoAdapter(vm.getRepos(), ac));
        adpf.addAdapter(new ActionAdapter(ac, Action.listOf(ac, R.id.action_nav_settings)));
        lvf.setAdapter(adpf);
        lvf.setOnItemClickListener((adapterView, view1, i, l) -> selectFolder(adapterView, i));

        widget(R.id.action_nav_account).setOnClickListener(view -> toggleView());
        OdsApp.bus.register(this, CompatEvent.PRIORITY_UI);
        updateCurrentAccount();
    }

    @Override
    public void onDestroyView() {
        OdsApp.bus.unregister(this);
        accounts.dispose();
        super.onDestroyView();
    }

    private void actionManage() {
        getMainActivity().getNavigation().openRootFolder(FragmentAccountList.class, null);
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

    public void onEventMainThread(EventSelectAccount val) {
        updateCurrentAccount();
    }

    private void updateCurrentAccount() {
        TextView tv = widget(R.id.action_nav_account);
        Account acc = OdsApp.get().getViewManager().getCurrentAccount();
        tv.setText(acc == null ? getString(R.string.nav_noaccount) : acc.getName());
    }

    private void toggleView() {
        ViewSwitcher vs = widget(R.id.view_nav_switch);
        TextView tv = widget(R.id.action_nav_account);
        vs.showNext();
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                vs.getDisplayedChild() == 1 ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down, 0);
    }

    private void selectAccount(AdapterView<?> view, int idx) {
        Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Account) {
            OdsApp.get().getViewManager().setCurrentAccount((Account) obj);
            toggleView();
        } else if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    private void executeAction(Action action) {
        switch (action.getId()) {
        case R.id.action_nav_manage:
            actionManage();
            break;

        case R.id.action_nav_settings:
            actionSettings();
            break;

        case R.id.action_nav_addaccount:
            actionAddAccount();
            break;
        }
    }

    private void selectFolder(AdapterView<?> view, int idx) {
        Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    private void actionAddAccount() {
        getMainActivity().getNavigation()
                .openFile(FragmentAccountDetails.class, new OperationAccountUpdate(new Account()));
    }
}
