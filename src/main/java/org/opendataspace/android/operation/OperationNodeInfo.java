package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

public class OperationNodeInfo extends OperationBase {

    @Expose
    private Node node;

    private final transient CmisSession session;

    public OperationNodeInfo(Node node, CmisSession session) {
        this.node = node;
        this.session = session;
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        result.setOk();
    }

    public Node getNode() {
        return node;
    }

    public CmisSession getSession() {
        return session;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
