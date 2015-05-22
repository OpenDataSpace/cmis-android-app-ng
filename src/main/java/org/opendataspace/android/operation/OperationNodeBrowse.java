package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

public class OperationNodeBrowse extends OperationBase {

    @Expose
    private final Node node;

    private final transient CmisSession session;

    public OperationNodeBrowse(Node node, CmisSession session) {
        this.node = node;
        this.session = session;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        status.setOk();
    }

    public Node getNode() {
        return node;
    }

    public CmisSession getSession() {
        return session;
    }
}
