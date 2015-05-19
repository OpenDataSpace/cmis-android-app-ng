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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.opendataspace.android.app.CompatEvent;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterMerge;
import org.opendataspace.android.event.EventAccountSelect;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.AccountAdapter;
import org.opendataspace.android.object.Action;
import org.opendataspace.android.object.ActionAdapter;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.object.RepoAdapter;
import org.opendataspace.android.operation.OperationAccountSelect;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationStatus;
import org.opendataspace.android.view.ViewManager;

public class FragmentNavigation extends FragmentBase implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private AccountAdapter accounts;
    private RepoAdapter repos;
    private final OperationAccountSelect op = new OperationAccountSelect(null);
    private boolean isMain = true;

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
        lva.setOnItemClickListener((adapterView, view1, i, l) -> selectItem(adapterView, i));

        DataAdapterMerge adpf = new DataAdapterMerge();
        adpf.addAdapter(repos = new RepoAdapter(vm.getRepos(), ac));
        adpf.addAdapter(new ActionAdapter(ac, Action.listOf(ac, R.id.action_nav_localfolder)));
        lvf.setAdapter(adpf);
        lvf.setOnItemClickListener((adapterView, view1, i, l) -> selectFolder(adapterView, i));

        widget(R.id.action_nav_account).setOnClickListener(view -> toggleView());
        widget(R.id.action_nav_accdesc).setOnClickListener(view -> toggleView());
        OdsApp.bus.register(this, CompatEvent.PRIORITY_UI);
        updateCurrentAccount();

        if (isMain && OdsApp.get().getViewManager().getCurrentAccount() == null) {
            selectAccount(null);
        }
    }

    @Override
    public void onDestroyView() {
        OdsApp.bus.unregister(this);
        accounts.dispose();
        repos.dispose();
        super.onDestroyView();
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

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventAccountSelect val) {
        updateCurrentAccount();
    }

    private void updateCurrentAccount() {
        TextView tva = widget(R.id.action_nav_account);
        TextView tvd = widget(R.id.action_nav_accdesc);
        Account acc = OdsApp.get().getViewManager().getCurrentAccount();
        tva.setText(acc == null ? getString(R.string.nav_noaccount) : acc.getDisplayName());
        tvd.setText(acc == null ? "" : acc.getDescription());
    }

    private void toggleView() {
        ViewSwitcher vs = widget(R.id.view_nav_switch);
        TextView tva = widget(R.id.action_nav_account);
        vs.showNext();
        tva.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                vs.getDisplayedChild() == 1 ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down, 0);
    }

    private void selectItem(AdapterView<?> view, int idx) {
        Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Account) {
            selectAccount((Account) obj);
            toggleView();
        } else if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    private void selectAccount(Account account) {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), null);
        op.setAccount(account);
        getLoaderManager().restartLoader(1, null, this);
    }

    private void executeAction(Action action) {
        switch (action.getId()) {
        case R.id.action_nav_manage:
            getMainActivity().getNavigation().openRootFolder(FragmentAccountList.class, null);
            break;

        case R.id.action_nav_settings:
            actionSettings();
            break;

        case R.id.action_nav_addaccount:
            getMainActivity().getNavigation()
                    .openFile(FragmentAccountDetails.class, new OperationAccountUpdate(new Account()));
            break;

        case R.id.action_nav_localfolder:
            getMainActivity().getNavigation().openRootFolder(FragmentFolderLibrary.class, null);
            break;
        }
    }

    private void selectFolder(AdapterView<?> view, int idx) {
        Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Repo) {
            getMainActivity().getNavigation().openRootFolder(FragmentFolderCmis.class,
                    new OperationFolderBrowse(OdsApp.get().getViewManager().getCurrentAccount(), (Repo) obj));
        } else if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int i, Bundle bundle) {
        return new OperationLoader(op, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus operationStatus) {
        getMainActivity().stopWait();
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        getMainActivity().stopWait();
    }

    public void setIsMain(boolean val) {
        isMain = val;
        setHasOptionsMenu(isMain);
    }
}
