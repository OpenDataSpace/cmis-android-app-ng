package org.opendataspace.android.operation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.gson.annotations.Expose;

import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
            res = fromContent(uri, result);
            break;
        }

        if (res) {
            result.setOk();
        }
    }

    private boolean fromFile(final String path, final OperationResult result) throws IOException {
        final Context context = OdsApp.get().getApplicationContext();
        final File f = new File(path);

        if (!f.exists() || !f.isFile()) {
            result.setError(context.getString(R.string.editor_notfound));
            return false;
        }

        if (f.length() > context.getResources().getInteger(R.integer.editor_maxsize)) {
            result.setError(context.getString(R.string.editor_toobig));
            return false;
        }

        text = IOUtils.readAllLines(new FileInputStream(f));
        file = f;
        return true;
    }

    private boolean fromContent(final Uri uri, final OperationResult result) throws IOException {
        Cursor cursor = null;

        try {
            cursor = OdsApp.get().getApplicationContext().getContentResolver()
                    .query(uri, new String[] {MediaStore.Files.FileColumns.DATA}, null, null, null);

            if (cursor == null) {
                return false;
            }

            final int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            return fromFile(cursor.getString(columnIndex), result);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public File getFile() {
        return file;
    }

    public String getText() {
        return text;
    }
}
