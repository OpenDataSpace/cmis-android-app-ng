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
        this.mime = isDirectory() ? null : mime.forFileName(getName());
    }

    public String getName() {
        return file.getName();
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

    public File getFile() {
        return file;
    }
}
