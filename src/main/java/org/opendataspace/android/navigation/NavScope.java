package org.opendataspace.android.navigation;

import com.google.gson.annotations.SerializedName;

public enum NavScope {
    @SerializedName("master")
    MAIN,
    @SerializedName("details")
    DETAILS,
    @SerializedName("dialog")
    DIALOG
}
