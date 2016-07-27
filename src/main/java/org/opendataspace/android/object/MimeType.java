package org.opendataspace.android.object;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import org.opendataspace.android.app.beta.R;

public class MimeType extends ObjectBase {

    public static final String FIELD_EXT = "ext";

    @Expose
    @DatabaseField(unique = true, columnName = FIELD_EXT, canBeNull = false)
    private String extension;

    @Expose
    @DatabaseField(columnName = "type", canBeNull = false)
    private String type;

    @Expose
    @DatabaseField(columnName = "is")
    private String icon;

    @Expose
    @DatabaseField(columnName = "desc", canBeNull = false)
    private String description;

    @SuppressWarnings("unused")
    public MimeType() {
        // nothing
    }

    public MimeType(String extension, String type, String description, String icon) {
        this.description = description;
        this.extension = extension;
        this.icon = icon;
        this.type = type;
    }

    public MimeType(final String extension, final String type) {
        this.extension = extension;
        this.icon = null;
        this.type = type;

        if (type.startsWith("audio/")) {
            description = "audio";
        } else if (type.startsWith("video/")) {
            description = "video";
        } else if (type.startsWith("text/")) {
            description = "text";
        } else if (type.startsWith("image/")) {
            description = "text";
        } else {
            description = "unknown";
        }
    }

    public String getDescription(Context context) {
        int res = context.getResources().getIdentifier("mime_" + description, "string", R.class.getPackage().getName());
        return res != 0 ? context.getString(res) : "";
    }

    public String getExtension() {
        return extension;
    }

    public int getIcon(Context context) {
        return TextUtils.isEmpty(icon) ? R.drawable.ic_file :
                context.getResources().getIdentifier("mime_" + icon, "drawable", R.class.getPackage().getName());
    }

    public String getType() {
        return type;
    }
}
