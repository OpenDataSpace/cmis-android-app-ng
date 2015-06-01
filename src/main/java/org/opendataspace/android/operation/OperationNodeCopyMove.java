package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.object.Node;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperationNodeCopyMove extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final List<Node> nodes = new ArrayList<>();

    @Expose
    private boolean isCopy;

    @Expose
    private Node target;

    public OperationNodeCopyMove(CmisSession session) {
        this.session = session;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        if (isEmpty() || target == null) {
            return;
        }

        boolean res = true;

        for (Node cur : nodes) {
            try {
                res &= processNode(cur, target, true);
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                status.setError(ex.getMessage());
                res = false;
            }
        }

        if (res) {
            status.setOk();
        }
    }

    private boolean processNode(Node node, Node to, boolean checkParent) throws SQLException {
        switch (node.getType()) {
        case DOCUMENT:
            return processDocument(node, to);

        case FOLDER:
            return processFolder(node, to, checkParent);

        default:
            return true;
        }
    }

    private boolean processFolder(Node node, Node to, boolean checkParent) throws SQLException {
        DaoNode dao = OdsApp.get().getDatabase().getNodes();

        if (checkParent) {
            Node p = to;

            while (p != null) {
                if (p.equals(node)) {
                    return false;
                }

                p = dao.get(p.getParentId());
            }
        }

        OperationFolderCreate create = new OperationFolderCreate(session, to, node.getName());
        create.execute();
        OperationFolderFetch fetch = new OperationFolderFetch(session, node);
        fetch.execute();
        CloseableIterator<Node> it = dao.forParent(session.getRepo(), node.getId());

        try {
            while (it.hasNext()) {
                if (!processNode(it.nextThrow(), create.getNode(), false)) {
                    return false;
                }
            }
        } finally {
            it.close();
        }

        if (!isCopy) {
            OperationNodeDelete del = new OperationNodeDelete(node, session);
            del.execute();
        }

        return true;
    }

    private boolean processDocument(Node node, Node to) throws SQLException {
        Document doc = (Document) node.getCmisObject(session);
        DaoNode dao = OdsApp.get().getDatabase().getNodes();
        CmisObject cmis;

        if (isCopy) {
            cmis = doc.copy(new ObjectIdImpl(to.getUuid()));
        } else {
            cmis = doc.move(new ObjectIdImpl(node.getParentUuid()), new ObjectIdImpl(to.getUuid()));
            dao.delete(node);
        }

        dao.create(new Node(cmis, to));
        return true;
    }

    public void setContext(List<Node> nodes, boolean isCopy) {
        this.nodes.clear();
        this.isCopy = isCopy;

        if (nodes != null) {
            this.nodes.addAll(nodes);
        }
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public boolean canPaste(Node node) {
        if (isEmpty()) {
            return false;
        }

        boolean hasDocument = false;
        boolean hasFolder = false;

        for (Node cur : nodes) {
            if (cur.equals(node) || node.getParentId() == cur.getId()) {
                return false;
            }

            switch (cur.getType()) {
            case DOCUMENT:
                hasDocument = true;
                break;

            case FOLDER:
                hasFolder = true;
                break;
            }

            if (hasFolder && hasDocument) {
                break;
            }
        }

        if (hasFolder && !node.canCreateFolder()) {
            return false;
        }

        if (hasDocument && !node.canCreateDocument()) {
            return false;
        }

        return true;
    }
}
