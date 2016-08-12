package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;

import java.util.Date;

class LinkInfo {

    @Expose
    public String name = "";

    @Expose
    public String url = "";

    @Expose
    public String message = "";

    @Expose
    public String email = "";

    @Expose
    public String password = "";

    @Expose
    public String objectId = "";

    @Expose
    public Date expires;
}
