package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.view.ViewNode;

public class OperationFolderBrowse extends OperationBase {

    @Expose
    private final Account account;
    @Expose
    private final Repo repo;
    @Expose
    private Node folder;

    public OperationFolderBrowse(Account account, Repo repo) {
        this.repo = repo;
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        OdsApp app = OdsApp.get();
        ViewNode nodes = app.getViewManager().getNodes();
        CmisSession session = nodes.setScope(account, repo, folder);
        nodes.sync(app.getDatabase().getNodes());

        if (!isCancel()) {
            app.getPool().execute(new OperationFolderFetch(session, folder));
        }

        status.setOk();
    }

    public void setFolder(Node folder) {
        this.folder = folder;
    }
}
