package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisOperations;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.event.EventAccountConfig;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
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

        CmisSession session = new CmisSession(account, repo);
        boolean res = checkFile(session, BRAND_ICON);
        res = checkFile(session, BRAND_LARGE) || res;

        if (res) {
            OdsApp.bus.post(new EventAccountConfig());
        }

        status.setOk();
    }

    private boolean checkFile(CmisSession session, String name) {
        try {
            if (isCancel()) {
                return false;
            }

            CmisObject cmis = session.getObjectByPath("branding/android/res/drawable-xxhdpi/" + name);

            if (cmis == null) {
                cmis = session.getObjectByPath("branding/android/res/drawable-xhdpi/" + name);
            }

            if (cmis == null) {
                cmis = session.getObjectByPath("branding/android/res/drawable/" + name);
            }

            if (cmis == null) {
                return false;
            }

            File f = Storage.getLocalFolder(OdsApp.get().getApplicationContext(), account, null, Storage.CATEGORY_CONFIG);

            if (f == null) {
                return false;
            }

            f = new File(f, name);

            if (f.exists() && f.length() == session.size(cmis)) {
                return false;
            }

            if (isCancel()) {
                return false;
            }

            CmisOperations.download(session, new Node(cmis, session.getRepo()), f);
            return true;
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        return false;
    }
}
