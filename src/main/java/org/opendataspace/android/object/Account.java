package org.opendataspace.android.object;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.net.MalformedURLException;
import java.net.URL;

@DatabaseTable(tableName = "acc")
public class Account extends ObjectBase {

    private static final String CMIS_JSON = "cmis/browser";
    private static final String CMIS_ATOM = "cmis/atom";

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = AccountSerializer.class)
    private final AccountInfo info = new AccountInfo();

    public String getLogin() {
        return info.login;
    }

    public void setLogin(String login) {
        info.login = login;
    }

    public String getPassword() {
        return info.password;
    }

    public void setPassword(String password) {
        info.password = password;
    }

    public String getName() {
        return info.name;
    }

    public void setName(String name) {
        info.name = name;
    }

    public boolean isUseJson() {
        return info.useJson;
    }

    public void setUseJson(boolean useJson) {
        info.useJson = useJson;
    }

    public Uri getUri() {
        boolean defaultPort =
                info.port == -1 || (info.useHttps && info.port == 80) || (!info.useHttps && info.port == 443);

        return new Uri.Builder().scheme(info.useHttps ? "https" : "http")
                .authority(info.host + (defaultPort ? "" : ":" + String.valueOf(info.port)))
                .path(TextUtils.isEmpty(info.path) ? (info.useJson ? CMIS_JSON : CMIS_ATOM) : info.path).build();
    }

    public String getDisplayUri() {
        if (TextUtils.isEmpty(info.host)) {
            return "";
        }

        boolean defaultPort =
                info.port == -1 || (info.useHttps && info.port == 80) || (!info.useHttps && info.port == 443);
        boolean defaultPath = TextUtils.isEmpty(info.path) || (info.useJson && CMIS_JSON.equals(info.path)) ||
                (!info.useJson && CMIS_ATOM.equals(info.path));

        StringBuilder b = new StringBuilder();

        if (!info.useHttps) {
            b.append("http://");
        }

        b.append(info.host);

        if (!defaultPort) {
            b.append(":").append(info.port);
        }

        if (!defaultPath) {
            if (!info.path.startsWith("/")) {
                b.append("/");
            }

            b.append(info.path);
        }

        return b.toString();
    }

    public void setUri(String val) throws MalformedURLException {
        URL url = new URL(TextUtils.isEmpty(val) ? "" : URLUtil.guessUrl(val));
        String path = url.getPath();

        if ("/".equals(path)) {
            path = "";
        }

        info.useHttps = "https".equals(url.getProtocol()) || !val.startsWith("http:");
        info.host = url.getHost();
        info.path = path;
        info.port = url.getPort();
    }

    public String getDescription() {
        return TextUtils.isEmpty(info.name) ? "" : getDisplayUri();
    }

    public String getDisplayName() {
        return TextUtils.isEmpty(info.name) ? getDisplayUri() : info.name;
    }

    public String getFolderName() {
        return info.login + "-" + info.host;
    }
}
