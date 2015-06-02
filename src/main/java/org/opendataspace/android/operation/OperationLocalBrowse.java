package org.opendataspace.android.operation;

import android.os.Build;
import android.os.Environment;
import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoMime;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationLocalBrowse extends OperationBase {

    public enum Mode {DEFAULT, SEL_FOLDER, SEL_FILES}

    @Expose
    private File root;

    @Expose
    private File top;

    @Expose
    private final Mode mode;

    @Expose
    private List<Node> context;

    @Expose
    private CmisSession session;

    private final transient List<FileInfo> data = new ArrayList<>();

    public OperationLocalBrowse(Mode mode) {
        this.mode = mode;
    }

    public void setFolder(File root) {
        this.root = root;

        if (top == null || root == null) {
            top = root;
        }
    }

    public File getFolder() {
        return root;
    }

    public File getTop() {
        return top;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        data.clear();

        if (root != null) {
            File[] ls = root.listFiles(cur -> !cur.isHidden());

            if (ls != null) {
                DaoMime mime = OdsApp.get().getDatabase().getMime();

                for (File cur : ls) {
                    data.add(new FileInfo(cur, mime));
                }
            }

            Collections.sort(data, (f1, f2) -> {
                int res = Boolean.valueOf(f1.isDirectory()).compareTo(f2.isDirectory());
                return res != 0 ? -res : f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
            });
        } else {
            data.add(new FileInfo(Environment.getExternalStorageDirectory(), R.id.action_local_root));
            addSpecial(Environment.DIRECTORY_DOWNLOADS, R.id.action_local_downloads);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addSpecial(Environment.DIRECTORY_DOCUMENTS, R.id.action_local_documents);
            }

            addSpecial(Environment.DIRECTORY_PICTURES, R.id.action_local_pictures);
            addSpecial(Environment.DIRECTORY_MUSIC, R.id.action_local_music);
            addSpecial(Environment.DIRECTORY_MOVIES, R.id.action_local_video);
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        status.setOk();
    }

    private void addSpecial(String type, int id) {
        File f = Environment.getExternalStoragePublicDirectory(type);

        if (f.exists()) {
            data.add(new FileInfo(f, id));
        }
    }

    public List<FileInfo> getData() {
        return data;
    }

    public Mode getMode() {
        return mode;
    }

    public List<Node> getContext() {
        return Collections.unmodifiableList(context);
    }

    public void setContext(List<Node> val) {
        if (context == null) {
            context = new ArrayList<>();
        }

        context.clear();
        context.addAll(val);
    }

    public CmisSession getSession() {
        return session;
    }

    public void setSession(CmisSession session) {
        this.session = session;
    }
}
