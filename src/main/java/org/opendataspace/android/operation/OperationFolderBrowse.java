package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.Task;
import org.opendataspace.android.app.TaskPool;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoMime;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.view.ViewNode;

public class OperationFolderBrowse extends OperationBase {

    @Expose
    private final Account account;

    @Expose
    private final Repo repo;

    @Expose
    private Node folder;

    @Expose
    private boolean cdup;

    private transient Task lastTask;
    private transient CmisSession session;

    public OperationFolderBrowse(Account account, Repo repo) {
        this.repo = repo;
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        OdsApp app = OdsApp.get();
        ViewNode nodes = app.getViewManager().getNodes();
        DaoNode dao = app.getDatabase().getNodes();

        if (folder != null && cdup) {
            long id = folder.getParentId();
            folder = id != ObjectBase.INVALID_ID ? dao.get(id) : null;
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        session = nodes.setScope(account, repo, folder);
        DaoMime mime = app.getDatabase().getMime();
        nodes.sync(dao);

        if (isCancel()) {
            throw new InterruptedException();
        }

        for (Node cur : nodes.getObjects()) {
            if (cur.getType() == Node.Type.DOCUMENT) {
                cur.setMimeType(mime.forFileName(cur.getName()));
            }
        }

        if (!isCancel()) {
            TaskPool pool = app.getPool();
            pool.cancel(lastTask);
            lastTask = pool.execute(new OperationFolderFetch(session, folder));
        }

        status.setOk();
    }

    public void setFolder(Node folder) {
        this.folder = folder;
    }

    public Node getFolder() {
        return folder;
    }

    public void setCdup(boolean val) {
        cdup = val;
    }

    public CmisSession getSession() {
        return session;
    }
}
