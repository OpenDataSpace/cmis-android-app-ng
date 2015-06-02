package org.opendataspace.android.operation;

import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;

import java.io.File;
import java.util.List;

public class OperationNodeDownload extends OperationBase {

    public OperationNodeDownload(CmisSession session, File folder, List<Node> context) {
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        status.setOk();
    }
}
