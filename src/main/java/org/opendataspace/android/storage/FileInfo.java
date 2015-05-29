package org.opendataspace.android.storage;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Formatter;

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

    public String getMimeDescription(Context context) {
        if (mime != null) {
            return mime.getDescription(context);
        }

        return context.getString(isDirectory() ? R.string.node_folder : R.string.node_unknown);
    }

    public String getNodeDecription(Context context) {
        String res = "";

        if (!isDirectory()) {
            res += Formatter.formatShortFileSize(context, file.length());
        }

        if (mime != null) {
            if (!res.isEmpty()) {
                res += " ";
            }

            res += mime.getDescription(context);
        }

        if (!res.isEmpty()) {
            res += " ";
        }

        res += DateUtils.getRelativeTimeSpanString(context, file.lastModified());
        return res;
    }

    public String getModifiedAt(Context context) {
        return DateFormat.getMediumDateFormat(context).format(file.lastModified()) + " " +
                DateFormat.getTimeFormat(context).format(file.lastModified());
    }

    public MimeType getMime() {
        return mime;
    }
}
