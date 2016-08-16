package org.opendataspace.android.object;

import android.text.TextUtils;

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

    public boolean update(final Link val) {
        boolean res = false;

        if (!TextUtils.equals(name, val.getName())) {
            name = val.getName();
            res = true;
        }

        if (!TextUtils.equals(url, val.getUrl())) {
            url = val.getUrl();
            res = true;
        }

        if (!TextUtils.equals(message, val.getMessage())) {
            message = val.getMessage();
            res = true;
        }

        if (!TextUtils.equals(email, val.getEmail())) {
            email = val.getEmail();
            res = true;
        }

        if (!TextUtils.equals(password, val.getPassword())) {
            password = val.getPassword();
            res = true;
        }

        return res;
    }
}
