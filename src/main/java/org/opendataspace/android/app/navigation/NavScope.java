package org.opendataspace.android.app.navigation;

import com.google.gson.annotations.SerializedName;

public enum NavScope {
    @SerializedName("master")
    MAIN,
    @SerializedName("details")
    DETAILS,
    @SerializedName("dialog")
    DIALOG
}
