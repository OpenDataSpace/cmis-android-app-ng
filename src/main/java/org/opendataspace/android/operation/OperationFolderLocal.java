package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoMime;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationFolderLocal extends OperationBase {

    @Expose
    private File root;

    @Expose
    private final File top;

    private final transient List<FileInfo> data = new ArrayList<>();

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
        data.clear();

        if (root == null) {
            return;
        }

        File[] ls = root.listFiles(cur -> !cur.isHidden());

        if (ls == null) {
            status.setOk();
            return;
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        DaoMime mime = OdsApp.get().getDatabase().getMime();

        for (File cur : ls) {
            data.add(new FileInfo(cur, mime));
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        Collections.sort(data, (f1, f2) -> {
            int res = Boolean.valueOf(f1.isDirectory()).compareTo(f2.isDirectory());
            return res != 0 ? -res : f1.getName().compareToIgnoreCase(f2.getName());
        });

        if (isCancel()) {
            throw new InterruptedException();
        }

        status.setOk();
    }

    public List<FileInfo> getData() {
        return data;
    }
}
