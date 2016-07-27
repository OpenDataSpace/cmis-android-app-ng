package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountDelete;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.operation.OperationBase;

import java.sql.SQLException;

@SuppressLint("ValidFragment")
public class FragmentAccountDetails extends FragmentBaseInput {

    private final OperationAccountUpdate op;

    public FragmentAccountDetails(final OperationAccountUpdate op) {
        final Account account = op.getAccount();
        this.op = op;

        addText(R.id.edit_account_host, account::getDisplayUri, account::setUri,
                val -> URLUtil.isValidUrl(URLUtil.guessUrl(val)));
        addText(R.id.edit_account_username, account::getLogin, account::setLogin, val -> !TextUtils.isEmpty(val));
        addText(R.id.edit_account_password, account::getPassword, account::setPassword, val -> !TextUtils.isEmpty(val));
        addImeDone(R.id.edit_account_description, account::getName, account::setName, null, this::actionApply);
        addBool(R.id.check_account_json, account::isUseJson, account::setUseJson);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public String getTile(final Context context) {
        return context.getString(R.string.account_title);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_account_details;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_account_apply:
            actionApply();
            break;

        case R.id.menu_account_cancel:
            getNavigation().backPressed();
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
        final ActivityMain ac = getMainActivity();

        if (ac.isWaiting() || !readAndValidate()) {
            return false;
        }

        if (!isDirty() && op.getAccount().isValidId()) {
            getNavigation().backPressed();
            return true;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), dialogInterface -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentAccountDetails::operationDone)).start();
        return true;
    }

    private void operationDone(final OperationBase op) {
        final ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (!op.reportError(ac)) {
            getNavigation().backPressed();
        }
    }

    private void actionDelete() {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        new AlertDialog.Builder(ac)
                .setMessage(String.format(getString(R.string.common_delete), op.getAccount().getName()))
                .setCancelable(true).setPositiveButton(R.string.common_ok, (di, i) -> doDelete())
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.cancel()).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mi = menu.findItem(R.id.menu_account_delete);

        if (mi != null) {
            mi.setVisible(op.getAccount().isValidId());
        }
    }

    @Override
    public boolean backPressed() {
        try {
            if (op.isFirstAccount() && OdsApp.get().getDatabase().getAccounts().countOf() == 0) {
                getActivity().finish();
                return true;
            }
        } catch (SQLException ex) {
            OdsLog.ex(getClass(), ex);
        }

        return false;
    }

    @Override
    public boolean needDrawer() {
        return !op.isFirstAccount();
    }

    private void doDelete() {
        final ActivityMain ac = getMainActivity();
        final OperationAccountDelete op = new OperationAccountDelete(this.op.getAccount());
        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), dialogInterface -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentAccountDetails::operationDone)).start();
    }
}
