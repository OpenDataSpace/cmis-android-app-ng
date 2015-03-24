package org.opendataspace.android.objects;

import com.google.gson.annotations.Expose;

class AccountInfo {
    @Expose
    public String host = "";

    @Expose
    public String login = "";

    @Expose
    public String password = "";

    @Expose
    public String name = "";

    @Expose
    public boolean useHttps = true;

    @Expose
    public boolean useJson = true;

    @Expose
    public int port = -1;

    @Expose
    public String path = "";
}
