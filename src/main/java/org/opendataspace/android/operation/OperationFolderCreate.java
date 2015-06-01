package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
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

    private transient Node node;

    public OperationFolderCreate(CmisSession session, Node parent, String name) {
        this.session = session;
        this.parent = parent;
        this.name = name;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        node = new Node(session.createFolder(parent, name), parent);
        OdsApp.get().getDatabase().getNodes().create(node);
        status.setOk();
    }

    public Node getNode() {
        return node;
    }
}
