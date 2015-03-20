package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import org.opendataspace.android.account.Account;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operations.OperationAccount;

import java.net.URL;

public class FragmentAccountDetails extends FragmentBaseInput {

    private Account account;
    private OperationAccount op;

    public FragmentAccountDetails(OperationAccount op) {
        this.account = op.getAccount();
        this.op = op;

        addText(R.id.edit_account_host, account::getDisplayUri, this::setUri,
                (String val) -> URLUtil.isValidUrl(URLUtil.guessUrl(val)));
        addText(R.id.edit_account_username, account::getLogin, account::setLogin,
                (String val) -> !TextUtils.isEmpty(val));
        addText(R.id.edit_account_password, account::getPassword, account::setPassword,
                (String val) -> !TextUtils.isEmpty(val));
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
        ActivityMain ac = (ActivityMain) getActivity();

        switch (item.getItemId()) {
        case R.id.menu_dialog_apply:
            if (readAndValidate()) {
                ac.getNavigation().backPressed(); // TODO execute operation
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
}
