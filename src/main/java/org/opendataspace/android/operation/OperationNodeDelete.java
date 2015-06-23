package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisOperations;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

import java.util.Collections;
import java.util.List;

public class OperationNodeDelete extends OperationBaseCmis {

    @Expose
    private final List<Node> nodes;

    @Expose
    private final CmisSession session;

    public OperationNodeDelete(List<Node> nodes, CmisSession session) {
        this.nodes = nodes;
        this.session = session;
    }

    public OperationNodeDelete(Node node, CmisSession session) {
        this.nodes = Collections.singletonList(node);
        this.session = session;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        boolean res = true;

        for (Node cur : nodes) {
            try {
                CmisOperations.deleteNode(session, cur);
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                status.setError(ex);
                res = false;
            }

            if (isCancel()) {
                throw new InterruptedException();
            }
        }

        if (res) {
            status.setOk();
        }
    }
}
