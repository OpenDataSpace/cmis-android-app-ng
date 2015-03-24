package org.opendataspace.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import org.opendataspace.android.objects.Account;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operations.OperationAccount;
import org.opendataspace.android.operations.OperationLoader;
import org.opendataspace.android.operations.OperationStatus;

import java.net.URL;

public class FragmentAccountDetails extends FragmentBaseInput
        implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final OperationAccount op;
    private ProgressDialog waitDialog;

    public FragmentAccountDetails(OperationAccount op) {
        Account account = op.getAccount();
        this.op = op;

        addText(R.id.edit_account_host, account::getDisplayUri, this::setUri,
                val -> URLUtil.isValidUrl(URLUtil.guessUrl(val)));
        addText(R.id.edit_account_username, account::getLogin, account::setLogin, val -> !TextUtils.isEmpty(val));
        addText(R.id.edit_account_password, account::getPassword, account::setPassword, val -> !TextUtils.isEmpty(val));
        addText(R.id.edit_account_description, account::getName, account::setName);
        addBool(R.id.check_account_atom, account::isUseJson, account::setUseJson);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.account_title);
    }

    private void setUri(String val) throws Exception {
        Account account = op.getAccount();
        URL url = new URL(URLUtil.guessUrl(val));

        account.setUseHttps("https".equals(url.getProtocol()));
        account.setHost(url.getHost());
        account.setPath(url.getPath());
        account.setPort(url.getPort());
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityMain ac = getMainActivity();

        switch (item.getItemId()) {
        case R.id.menu_dialog_apply:
            if (readAndValidate()) {
                waitDialog = ProgressDialog.show(ac, getTile(ac), getString(R.string.common_pleasewait), true, true,
                        dialogInterface -> getLoaderManager().destroyLoader(0));

                getLoaderManager().initLoader(0, null, this);
            }
            break;

        case R.id.menu_dialog_cancel:
            ac.getNavigation().backPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        return new OperationLoader(op, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        if (waitDialog != null) {
            waitDialog.hide();
            waitDialog = null;
        }

        ActivityMain ac = getMainActivity();

        if (data.isOk()) {
            ac.getNavigation().backPressed();
        } else {
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        if (waitDialog != null) {
            waitDialog.hide();
            waitDialog = null;
        }
    }
}
