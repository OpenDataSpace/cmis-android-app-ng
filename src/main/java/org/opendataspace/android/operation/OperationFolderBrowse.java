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
import org.opendataspace.android.storage.FileInfo;
import org.opendataspace.android.view.ViewNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationFolderBrowse extends OperationBase {

    public enum Mode {DEFAULT, SEL_FOLDER, SEL_FILES}

    @Expose
    private final Account account;

    @Expose
    private Repo repo;

    @Expose
    private Node folder;

    @Expose
    private boolean cdup;

    @Expose
    private final Mode mode;

    @Expose
    private List<FileInfo> context;

    private transient Task lastTask;
    private transient CmisSession session;
    private transient final ViewNode view;

    public OperationFolderBrowse(Account account, Repo repo, Mode mode) {
        this.repo = repo;
        this.account = account;
        this.mode = mode;
        view = OdsApp.get().getViewManager().createNodeView();
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        OdsApp app = OdsApp.get();
        DaoNode dao = app.getDatabase().getNodes();

        if (folder != null && cdup) {
            long id = folder.getParentId();
            folder = id != ObjectBase.INVALID_ID ? dao.get(id) : null;
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        session = view.setScope(account, repo, folder);

        if (folder == null) {
            folder = new Node(repo);
        }

        DaoMime mime = app.getDatabase().getMime();
        view.sync(dao);

        if (isCancel()) {
            throw new InterruptedException();
        }

        for (Node cur : view.getObjects()) {
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

    public Mode getMode() {
        return mode;
    }

    public List<FileInfo> getContext() {
        return Collections.unmodifiableList(context);
    }

    public void setContext(List<FileInfo> val) {
        if (context == null) {
            context = new ArrayList<>();
        }

        context.clear();
        context.addAll(val);
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public Repo getRepo() {
        return repo;
    }

    public ViewNode getView() {
        return view;
    }
}
