package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisOperations;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OperationNodeDownload extends OperationBaseCmis {

    @Expose
    private final CmisSession session;

    @Expose
    private final File folder;

    @Expose
    private final List<Node> context = new ArrayList<>();

    public OperationNodeDownload(CmisSession session, File folder, List<Node> context) {
        this.session = session;
        this.folder = folder;
        this.context.addAll(context);
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        boolean res = true;

        for (Node cur : context) {
            try {
                final File src = OdsApp.get().getCacheManager().getLocal(cur);
                final File dest = new File(folder, cur.getName());

                if (src != null) {
                    res = res && Storage.copyFile(src, dest);
                } else {
                    res = res && CmisOperations.download(session, cur, dest, getStatus());
                }
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
