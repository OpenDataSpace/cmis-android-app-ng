package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

public class OperationNodeDelete extends OperationBaseCmis {

    @Expose
    private final Node node;

    @Expose
    private final CmisSession session;

    public OperationNodeDelete(Node node, CmisSession session) {
        this.node = node;
        this.session = session;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        session.delete(node.getUuid());
        OdsApp.get().getDatabase().getNodes().delete(node);
        status.setOk();
    }
}
