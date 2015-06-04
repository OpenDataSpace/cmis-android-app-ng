package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

import java.io.File;

public class OperationNodeOpen extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node node;

    private transient File file;

    public OperationNodeOpen(CmisSession session, Node node) {
        this.session = session;
        this.node = node;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        CmisObject cmis = node.getCmisObject(session);
        OdsApp app = OdsApp.get();

        if (node.merge(cmis)) {
            app.getDatabase().getNodes().update(node);
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        file = app.getCacheManager().download(session, node);
        status.setOk();
    }

    public File getFile() {
        return file;
    }
}
