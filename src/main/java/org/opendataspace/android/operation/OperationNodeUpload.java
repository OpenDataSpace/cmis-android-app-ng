package org.opendataspace.android.operation;

import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.storage.FileInfo;

import java.util.List;

public class OperationNodeUpload extends OperationBaseCmis {

    public OperationNodeUpload(CmisSession session, Node folder, List<FileInfo> context) {
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        status.setOk();
    }
}
