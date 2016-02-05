package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.storage.FileInfo;

public class OperationLocalInfo extends OperationBase {

    @Expose
    private FileInfo file;

    public OperationLocalInfo(FileInfo file) {
        this.file = file;
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        result.setOk();
    }

    public FileInfo getFileInfo() {
        return file;
    }

    public void setInfo(FileInfo info) {
        file = info;
    }
}
