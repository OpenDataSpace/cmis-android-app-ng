package org.opendataspace.android.operation;

import android.content.Context;
import android.net.Uri;

import com.google.gson.annotations.Expose;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;

import java.io.File;

public class OperationLoadText extends OperationBase {

    @Expose
    private final String uri;

    private transient File file;
    private transient String text;

    public OperationLoadText(final String uri) {
        this.uri = uri;
    }

    @Override
    protected void doExecute(final OperationResult result) throws Exception {
        final Uri uri = Uri.parse(this.uri);
        boolean res = false;

        switch (uri.getScheme()) {
        case "file":
            res = fromFile(uri.getPath(), result);
            break;

        case "content":
            break;
        }

        if (res) {
            result.setOk();
        }
    }

    private boolean fromFile(final String path, final OperationResult result) {
        final Context context = OdsApp.get().getApplicationContext();
        file = new File(path);

        if (!file.exists()) {
            result.setError(context.getString(R.string.editor_notfound));
            return false;
        }

        if (file.length() > context.getResources().getInteger(R.integer.editor_maxsize)) {
            result.setError(context.getString(R.string.editor_toobig));
            return false;
        }

        text = "";
        return true;
    }

    public File getFile() {
        return file;
    }

    public String getText() {
        return text;
    }
}
