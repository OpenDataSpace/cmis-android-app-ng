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
    private String extenstion;

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

    public MimeType(String extenstion, String type, String description, String icon) {
        this.description = description;
        this.extenstion = extenstion;
        this.icon = icon;
        this.type = type;
    }

    public String getDescription(Context context) {
        int res = context.getResources().getIdentifier("mime_" + description, "string", R.class.getPackage().getName());
        return res != 0 ? context.getString(res) : "";
    }

    public String getExtenstion() {
        return extenstion;
    }

    public int getIcon(Context context) {
        return TextUtils.isEmpty(icon) ? R.drawable.ic_file :
                context.getResources().getIdentifier("file_" + icon, "drawable", R.class.getPackage().getName());
    }

    public String getType() {
        return type;
    }
}
