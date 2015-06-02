package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.storage.FileInfo;
import org.opendataspace.android.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OperationLocalCopyMove extends OperationBase {

    @Expose
    private final List<FileInfo> nodes = new ArrayList<>();

    @Expose
    private boolean isCopy;

    @Expose
    private File target;

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        boolean res = true;

        for (FileInfo cur : nodes) {
            try {
                File source = cur.getFile();
                File dest = new File(target, source.getName());

                if (isCopy) {
                    res &= Storage.copyFile(source, dest);
                } else {
                    res &= source.renameTo(dest);
                }
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                status.setError(ex.getMessage());
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

    public void setContext(List<FileInfo> ls, boolean isCopy) {
        nodes.clear();
        this.isCopy = isCopy;

        if (ls != null) {
            nodes.addAll(ls);
        }
    }

    public void setTarget(File target) {
        this.target = target;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
