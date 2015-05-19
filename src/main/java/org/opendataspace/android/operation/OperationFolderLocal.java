package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;

import java.io.File;

public class OperationFolderLocal extends OperationBase {

    @Expose
    private File root;

    @Expose
    private final File top;

    public OperationFolderLocal(File root) {
        top = root;
        setRoot(root);
    }

    public void setRoot(File root) {
        this.root = root;
    }

    public File getRoot() {
        return root;
    }

    public File getTop() {
        return top;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        // nothing
    }
}
