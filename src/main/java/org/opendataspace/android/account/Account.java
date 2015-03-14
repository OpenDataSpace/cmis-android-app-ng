package org.opendataspace.android.account;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "acc")
public class Account {

    @DatabaseField(id = true, columnName = "id")
    private long id;
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = AccountSerializer.class)
    private AccountInfo info = new AccountInfo();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHost() {
        return info.host;
    }

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

    public boolean isUseHttps() {
        return info.useHttps;
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

    public int getPort() {
        return info.port;
    }

    public void setPort(int port) {
        info.port = port;
    }

    public String getPath() {
        return info.path;
    }

    public void setPath(String path) {
        info.path = path;
    }
}
