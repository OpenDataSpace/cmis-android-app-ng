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
    protected void doExecute(OperationResult result) throws Exception {
        boolean res = true;

        for (FileInfo cur : nodes) {
            try {
                File source = cur.getFile();
                File dest = new File(target, source.getName());

                if (isCopy) {
                    res = res && Storage.copyFile(source, dest);
                } else {
                    res = res && source.renameTo(dest);
                }
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                result.setError(ex);
                res = false;
            }

            if (isCancel()) {
                throw new InterruptedException();
            }
        }

        if (res) {
            result.setOk();
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

    public List<FileInfo> getNodes() {
        return nodes;
    }

    public boolean willCopy() {
        return isCopy;
    }
}
