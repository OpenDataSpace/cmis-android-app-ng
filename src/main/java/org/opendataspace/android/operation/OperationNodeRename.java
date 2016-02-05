package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

public class OperationNodeRename extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node node;

    @Expose
    private final String name;

    public OperationNodeRename(CmisSession session, Node node, String name) {
        this.session = session;
        this.node = node;
        this.name = name;
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        node.getCmisObject(session, getStatus()).rename(name);
        node.setName(name);
        OdsApp.get().getDatabase().getNodes().update(node);
        result.setOk();
    }
}
