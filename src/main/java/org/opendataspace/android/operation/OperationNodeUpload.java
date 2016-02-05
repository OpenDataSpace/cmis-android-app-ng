package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.storage.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class OperationNodeUpload extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node folder;

    @Expose
    private final List<FileInfo> context = new ArrayList<>();

    public OperationNodeUpload(CmisSession session, Node folder, List<FileInfo> context) {
        this.session = session;
        this.folder = folder;
        this.context.addAll(context);
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        boolean res = true;

        for (FileInfo cur : context) {
            try {
                CmisObject cmis = session.createDocument(folder, cur.getFile().getName(), cur, getStatus());
                OdsApp.get().getDatabase().getNodes().create(new Node(cmis, folder));
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                res = false;
                result.setError(ex);
            }

            if (isCancel()) {
                throw new InterruptedException();
            }
        }


        if (res) {
            result.setOk();
        }
    }
}
