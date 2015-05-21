package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.object.Node;

public class OperationNodeBrowse extends OperationBase {

    @Expose
    private final Node node;

    public OperationNodeBrowse(Node node) {
        this.node = node;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        status.setOk();
    }

    public Node getNode() {
        return node;
    }
}
