package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.event.EventAccountConfig;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.storage.Storage;

import java.io.File;

public class OperationAccountConfig extends OperationBase {

    public static final String BRAND_ICON = "ic_logo.png";
    public static final String BRAND_LARGE = "logo_large.png";

    @Expose
    private final Account account;

    public OperationAccountConfig(Account account) {
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        Repo repo = OdsApp.get().getDatabase().getRepos().getConfig(account);

        if (repo == null) {
            return;
        }

        CmisSession ses = new CmisSession(account, repo);
        boolean res = checkFile(ses, BRAND_ICON);
        res = checkFile(ses, BRAND_LARGE) && res;

        if (res) {
            OdsApp.bus.post(new EventAccountConfig());
        }

        status.setOk();
    }

    private boolean checkFile(CmisSession ses, String name) {
        try {
            if (isCancel()) {
                return false;
            }

            CmisObject cmo = ses.getObject("branding/android/res/drawable-xxhdpi/" + name);

            if (cmo == null) {
                cmo = ses.getObject("branding/android/res/drawable-xhdpi/" + name);
            }

            if (cmo == null) {
                cmo = ses.getObject("branding/android/res/drawable/" + name);
            }

            if (cmo == null) {
                return false;
            }

            File f = Storage.getConfigFile(OdsApp.get().getApplicationContext(), name, account);

            if (f != null && f.exists() && f.length() == ses.size(cmo)) {
                return false;
            }

            if (isCancel()) {
                return false;
            }

            ses.save(cmo, f);
            return true;
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        return false;
    }
}
