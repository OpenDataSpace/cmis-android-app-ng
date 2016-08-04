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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterMerge;
import org.opendataspace.android.event.Event;
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
import org.opendataspace.android.operation.OperationLocalBrowse;
import org.opendataspace.android.view.ViewManager;

public class FragmentNavigation extends FragmentBase {

    private AccountAdapter accounts;
    private RepoAdapter repos;
    private final OperationAccountSelect op = new OperationAccountSelect(null);
    private boolean isMain = true;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity ac = getActivity();
        final ViewManager vm = OdsApp.get().getViewManager();
        final ListView lva = widget(R.id.list_nav_accounts);
        final ListView lvf = widget(R.id.list_nav_folders);

        final DataAdapterMerge adpa = new DataAdapterMerge();
        adpa.addAdapter(accounts = new AccountAdapter(vm.getAccounts(), ac));
        adpa.addAdapter(new ActionAdapter(ac, Action.listOf(ac, R.id.action_nav_addaccount, R.id.action_nav_manage),
                R.layout.delegate_list_item2));
        lva.setAdapter(adpa);
        lva.setOnItemClickListener((adapterView, view1, i, l) -> selectItem(adapterView, i));

        final DataAdapterMerge adpf = new DataAdapterMerge();
        adpf.addAdapter(repos = new RepoAdapter(vm.getRepos(), ac));
        adpf.addAdapter(
                new ActionAdapter(ac, Action.listOf(ac, R.id.action_nav_localfolder), R.layout.delegate_list_item1));
        lvf.setAdapter(adpf);
        lvf.setOnItemClickListener((adapterView, view1, i, l) -> selectFolder(adapterView, i));

        widget(R.id.action_nav_account).setOnClickListener(view -> toggleView());
        widget(R.id.action_nav_accdesc).setOnClickListener(view -> toggleView());
        OdsApp.bus.register(this);
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        final ActivityMain ac = getMainActivity();

        if (ac == null) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
        case R.id.menu_main_about:
            getNavigation().openDialog(FragmentAbout.class, null);
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
        getNavigation().openDialog(FragmentSettings.class, null);
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_UI)
    public void onEvent(final EventAccountSelect val) {
        updateCurrentAccount();
    }

    private void updateCurrentAccount() {
        final TextView tva = widget(R.id.action_nav_account);
        final TextView tvd = widget(R.id.action_nav_accdesc);
        final Account acc = OdsApp.get().getViewManager().getCurrentAccount();

        accounts.exclude(acc);
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

    private void selectItem(final AdapterView<?> view, final int idx) {
        final Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Account) {
            selectAccount((Account) obj);
            toggleView();
        } else if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    private void selectAccount(final Account account) {
        final ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), null);
        op.setAccount(account);
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentNavigation::operationDone)).start();
    }

    @SuppressWarnings("UnusedParameters")
    private void operationDone(final OperationAccountSelect op) {
        getMainActivity().stopWait();
    }

    private void executeAction(final Action action) {
        switch (action.getId()) {
        case R.id.action_nav_manage:
            getNavigation().openRootFolder(FragmentAccountList.class, null);
            break;

        case R.id.action_nav_addaccount:
            getNavigation().openFile(FragmentAccountDetails.class, new OperationAccountUpdate(new Account()));
            break;

        case R.id.action_nav_localfolder:
            getNavigation().openRootFolder(FragmentFolderLocal.class,
                    new OperationLocalBrowse(OdsApp.get().getViewManager().getCurrentAccount(),
                            OperationLocalBrowse.Mode.DEFAULT));
            break;
        }
    }

    private void selectFolder(final AdapterView<?> view, final int idx) {
        final Object obj = view.getAdapter().getItem(idx);

        if (obj instanceof Repo) {
            getNavigation().openRootFolder(FragmentFolderCmis.class,
                    new OperationFolderBrowse(OdsApp.get().getViewManager().getCurrentAccount(), (Repo) obj,
                            OperationFolderBrowse.Mode.DEFAULT));
        } else if (obj instanceof Action) {
            executeAction((Action) obj);
        }
    }

    public void setNonMain() {
        isMain = false;
        setHasOptionsMenu(false);
    }
}
