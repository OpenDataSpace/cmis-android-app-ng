package org.opendataspace.android.storage;

import android.content.Context;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DaoMime;
import org.opendataspace.android.object.MimeType;

import java.io.File;
import java.sql.SQLException;

public class FileInfo {

    private final File file;
    private final MimeType mime;

    public FileInfo(File file, DaoMime mime) throws SQLException {
        this.file = file;
        this.mime = isDirectory() ? null : mime.forExtension(getExtension());
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public int getIcon(Context context) {
        if (mime != null) {
            return mime.getIcon(context);
        }

        return file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getExtension() {
        String name = getName();
        int dot = name.lastIndexOf(".".charAt(0));
        return dot > 0 ? name.substring(dot + 1).toLowerCase() : "";
    }

    public File getFile() {
        return file;
    }
}
