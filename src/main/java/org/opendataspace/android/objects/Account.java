package org.opendataspace.android.objects;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "acc")
public class Account {

    private static final String CMIS_JSON = "cmis/browser";
    private static final String CMIS_ATOM = "service/cmis";

    @Expose
    @DatabaseField(id = true, columnName = "id")
    private long id;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = AccountSerializer.class)
    private final AccountInfo info = new AccountInfo();

    public void setHost(String host) {
        info.host = host;
    }

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

    public void setUseHttps(boolean useHttps) {
        info.useHttps = useHttps;
    }

    public boolean isUseJson() {
        return info.useJson;
    }

    public void setUseJson(boolean useJson) {
        info.useJson = useJson;
    }

    public void setPort(int port) {
        info.port = port;
    }

    public void setPath(String path) {
        info.path = path;
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

        if (info.useHttps) {
            b.append("https://");
        }

        b.append(info.host);

        if (!defaultPort) {
            b.append(":").append(info.port);
        }

        if (!defaultPath) {
            b.append("/").append(info.path);
        }

        return b.toString();
    }
}
