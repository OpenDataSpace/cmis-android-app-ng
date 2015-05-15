package org.opendataspace.android.operation;

import org.opendataspace.android.object.Repo;

public class OperationFolderBrowse extends OperationBase {

    private Repo repo;

    public OperationFolderBrowse(Repo repo) {
        this.repo = repo;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
    }
}
