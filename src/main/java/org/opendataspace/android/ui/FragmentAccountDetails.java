package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountDelete;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationStatus;

@SuppressLint("ValidFragment")
public class FragmentAccountDetails extends FragmentBaseInput
        implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final static int LOADER_APPLY = 1;
    private final static int LOADER_DELETE = 2;

    private final OperationAccountUpdate op;

    public FragmentAccountDetails(OperationAccountUpdate op) {
        Account account = op.getAccount();
        this.op = op;

        addText(R.id.edit_account_host, account::getDisplayUri, account::setUri,
                val -> URLUtil.isValidUrl(URLUtil.guessUrl(val)));
        addText(R.id.edit_account_username, account::getLogin, account::setLogin, val -> !TextUtils.isEmpty(val));
        addText(R.id.edit_account_password, account::getPassword, account::setPassword, val -> !TextUtils.isEmpty(val));
        addImeDone(R.id.edit_account_description, account::getName, account::setName, null, this::actionApply);
        addBool(R.id.check_account_json, account::isUseJson, account::setUseJson);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.account_title);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_account_details;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_account_apply:
            actionApply();
            break;

        case R.id.menu_account_cancel:
            getMainActivity().getNavigation().backPressed();
            break;

        case R.id.menu_account_delete:
            actionDelete();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean actionApply() {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting() || !readAndValidate()) {
            return false;
        }

        if (!isDirty() && op.getAccount().isValidId()) {
            ac.getNavigation().backPressed();
            return true;
        }

        startLoader(LOADER_APPLY);
        return true;
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case LOADER_APPLY:
            return new OperationLoader(op, getActivity());

        case LOADER_DELETE:
            return new OperationLoader(new OperationAccountDelete(op.getAccount()), getActivity());

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (data.isOk()) {
            ac.getNavigation().backPressed();
        } else {
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        getMainActivity().stopWait();
    }

    private void actionDelete() {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        new AlertDialog.Builder(ac)
                .setMessage(String.format(getString(R.string.accout_delete), op.getAccount().getName()))
                .setCancelable(true).setPositiveButton(R.string.common_ok, (di, i) -> startLoader(LOADER_DELETE))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.cancel()).show();
    }

    private void startLoader(int id) {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait),
                di -> getLoaderManager().destroyLoader(id));

        getLoaderManager().restartLoader(id, null, this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mi = menu.findItem(R.id.menu_account_delete);

        if (mi != null) {
            mi.setVisible(op.getAccount().isValidId());
        }
    }
}
