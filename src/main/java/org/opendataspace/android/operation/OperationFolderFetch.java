package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.event.EventNodeUpdate;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.storage.CacheRemoveTransaction;

import java.sql.SQLException;
import java.util.List;

public class OperationFolderFetch extends OperationBaseFetch<Node, CmisObject> {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node folder;

    private transient final CacheRemoveTransaction remove = new CacheRemoveTransaction();

    public OperationFolderFetch(CmisSession session, Node folder) {
        this.session = session;
        this.folder = folder;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        process(this, this);
        OdsApp.bus.post(new EventNodeUpdate());
        status.setOk();
    }

    @Override
    protected CloseableIterator<Node> localObjects() throws SQLException {
        return OdsApp.get().getDatabase().getNodes().forParent(session.getRepo(), folder.getId());
    }

    @Override
    protected List<CmisObject> fetch() {
        return session.children(folder);
    }

    @Override
    protected Node find(CmisObject val, List<Node> ls) {
        String uuid = val.getId();

        for (Node cur : ls) {
            if (cur.getUuid().equals(uuid)) {
                return cur;
            }
        }

        return null;
    }

    @Override
    protected void create(CmisObject val) throws SQLException {
        Node node = new Node(val, folder);

        if (node.getType() == Node.Type.DOCUMENT) {
            node.setMimeType(OdsApp.get().getDatabase().getMime().forFileName(node.getName()));
        }

        OdsApp.get().getDatabase().getNodes().create(node);
    }

    @Override
    protected void merge(Node node, CmisObject val) throws SQLException {
        boolean res = node.merge(val);

        if (res && node.getMimeType() == null && node.getType() == Node.Type.DOCUMENT) {
            node.setMimeType(OdsApp.get().getDatabase().getMime().forFileName(node.getName()));
        }

        if (res) {
            OdsApp.get().getDatabase().getNodes().update(node);
        }
    }

    @Override
    protected void delete(Node obj) throws SQLException {
        OdsApp app = OdsApp.get();
        DaoNode nodes = app.getDatabase().getNodes();
        nodes.delete(obj);
        app.getCacheManager().prepareRemove(nodes, session.getRepo(), obj, remove);
    }

    @Override
    protected void cleanup(List<Node> ls) throws SQLException {
        remove.commit();
    }
}
