package org.opendataspace.android.storage;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DaoMime;
import org.opendataspace.android.object.MimeType;
import org.opendataspace.android.object.ObjectBaseId;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class FileInfo implements ObjectBaseId {

    private final static AtomicInteger counter = new AtomicInteger(0);

    private final File file;
    private final MimeType mime;
    private final int special;
    private final long id;

    public FileInfo(File file, DaoMime mime) throws SQLException {
        this.file = file;
        this.mime = (isDirectory() || mime == null) ? null : mime.forFileName(file.getName());
        this.special = 0;
        id = counter.incrementAndGet();
    }

    public FileInfo(File file, int special) {
        this.file = file;
        this.mime = null;
        this.special = special;
        id = counter.incrementAndGet();
    }

    public String getName(Context context) {
        switch (special) {
        case R.id.action_local_root:
            return context.getString(R.string.folder_root);

        case R.id.action_local_downloads:
            return context.getString(R.string.folder_downloads);

        case R.id.action_local_documents:
            return context.getString(R.string.folder_documents);

        case R.id.action_local_pictures:
            return context.getString(R.string.folder_pictures);

        case R.id.action_local_music:
            return context.getString(R.string.folder_music);

        case R.id.action_local_video:
            return context.getString(R.string.folder_video);

        default:
            return file.getName();
        }
    }

    public int getIcon(Context context) {
        if (mime != null) {
            return mime.getIcon(context);
        }

        switch (special) {
        case R.id.action_local_root:
            return R.drawable.ic_phone;

        case R.id.action_local_downloads:
            return R.drawable.ic_downloads;

        case R.id.action_local_documents:
            return R.drawable.ic_documents;

        case R.id.action_local_pictures:
            return R.drawable.ic_pictures;

        case R.id.action_local_music:
            return R.drawable.ic_music;

        case R.id.action_local_video:
            return R.drawable.ic_videos;
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

    @Override
    public long getId() {
        return id;
    }
}
