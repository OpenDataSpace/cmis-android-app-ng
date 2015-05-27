package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

public class OperationFolderCreate extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node parent;

    @Expose
    private final String name;

    private transient String lastId;

    public OperationFolderCreate(CmisSession session, Node parent, String name) {
        this.session = session;
        this.parent = parent;
        this.name = name;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        CmisObject cmis = session.createFolder(parent, name);
        lastId = cmis.getId();
        Node node;

        if (parent != null) {
            node = new Node(cmis, parent);
        } else {
            node = new Node(cmis, session.getRepo());
        }

        OdsApp.get().getDatabase().getNodes().create(node);
        status.setOk();
    }

    public String getLastUuid() {
        return lastId;
    }
}
