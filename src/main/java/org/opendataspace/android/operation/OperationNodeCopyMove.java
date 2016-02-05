package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisOperations;
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
    protected void doExecute(OperationResult result) throws Exception {
        if (isEmpty() || target == null) {
            return;
        }

        boolean res = true;

        for (Node cur : nodes) {
            try {
                res = res && processNode(cur, target, true);
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                result.setError(ex);
                res = false;
            }

            if (isCancel()) {
                throw new InterruptedException();
            }
        }

        if (res) {
            result.setOk();
        }
    }

    private boolean processNode(Node node, Node to, boolean checkParent) throws Exception {
        if (isCancel()) {
            return false;
        }

        switch (node.getType()) {
        case FOLDER:
            return processFolder(node, to, checkParent);

        case DOCUMENT:
            processDocument(node, to);
            // no break

        default:
            return true;
        }
    }

    private boolean processFolder(Node node, Node to, boolean checkParent) throws Exception {
        DaoNode dao = OdsApp.get().getDatabase().getNodes();

        if (checkParent) {
            Node p = to;

            while (p != null) {
                if (p.equals(node) || isCancel()) {
                    return false;
                }

                p = dao.get(p.getParentId());
            }
        }

        Node folder = CmisOperations.createFolder(session, to, node.getName(), getStatus());
        OperationFolderFetch.process(new OperationFolderFetch(session, node), this);
        CloseableIterator<Node> it = dao.forParent(session.getRepo(), node.getId());

        try {
            while (it.hasNext()) {
                if (!processNode(it.nextThrow(), folder, false)) {
                    return false;
                }
            }
        } finally {
            it.close();
        }

        if (!isCopy) {
            CmisOperations.deleteNode(session, node, getStatus());
        }

        return true;
    }

    private void processDocument(Node node, Node to) throws SQLException {
        Document doc = (Document) node.getCmisObject(session, getStatus());
        DaoNode dao = OdsApp.get().getDatabase().getNodes();

        if (isCopy) {
            CmisObject cmis = doc.copy(new ObjectIdImpl(to.getUuid()));
            dao.create(new Node(cmis, to));
        } else {
            CmisObject cmis = doc.move(new ObjectIdImpl(node.getParentUuid()), new ObjectIdImpl(to.getUuid()));
            node.merge(cmis);
            node.setParentId(to.getId());
            dao.update(node);
        }
    }

    public void setContext(List<Node> nodes, boolean isCopy) {
        this.nodes.clear();
        this.isCopy = isCopy;

        if (nodes != null) {
            this.nodes.addAll(nodes);
        }
    }

    private boolean isEmpty() {
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
            if (cur.equals(node) || node.getParentId() == cur.getId() || cur.getParentId() == node.getId()) {
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

        return !(hasFolder && !node.canCreateFolder()) && !(hasDocument && !node.canCreateDocument());
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public boolean willCopy() {
        return isCopy;
    }
}
