package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.RepoAdapter;
import org.opendataspace.android.operation.OperationFolderBrowse;

@SuppressLint("ValidFragment")
public class FragmentRepoList extends FragmentBaseList {

    private final OperationFolderBrowse op;
    private RepoAdapter adapter;

    public FragmentRepoList(OperationFolderBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RepoAdapter(OdsApp.get().getViewManager().getRepos(), getActivity());
        setListAdapter(adapter);
        setEmptyText(getString(R.string.nav_norepo));
    }

    @Override
    public void onDestroyView() {
        adapter.dispose();
        super.onDestroyView();
    }

    @Override
    public String getTile(Context context) {
        switch (op.getMode()) {
        case SEL_FOLDER:
            return context.getString(R.string.folder_pickfolder);

        case SEL_FILES:
            return context.getString(R.string.folder_pickfile);

        default:
            return super.getTile(context);
        }
    }

    @Override
    void onListItemClick(int position) {
        op.setRepo(adapter.getObject(position));
        getNavigation().openDialog(FragmentFolderCmis.class, op);
    }
}
