package org.opendataspace.android.navigation;

import com.google.gson.annotations.SerializedName;

enum NavigationScope {

    @SerializedName("master")
    MAIN,

    @SerializedName("details")
    DETAILS,

    @SerializedName("dialog")
    DIALOG
}
