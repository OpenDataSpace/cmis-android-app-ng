package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
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
        for (Node cur : nodes) {
            try {
                session.delete(cur);
                OdsApp.get().getDatabase().getNodes().delete(cur);
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        status.setOk();
    }
}
