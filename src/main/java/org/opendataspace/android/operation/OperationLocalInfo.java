package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.storage.FileInfo;

public class OperationLocalInfo extends OperationBase {

    @Expose
    private FileInfo file;

    @Expose
    private final Account account;

    public OperationLocalInfo(FileInfo file, Account account) {
        this.file = file;
        this.account = account;
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        result.setOk();
    }

    public FileInfo getFileInfo() {
        return file;
    }

    public void setInfo(FileInfo info) {
        file = info;
    }

    public Account getAccount() {
        return account;
    }
}
