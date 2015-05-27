package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Action;
import org.opendataspace.android.object.ActionAdapter;
import org.opendataspace.android.operation.OperationFolderLocal;

import java.io.File;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FragmentFolderLibrary extends FragmentBaseList {

    private ActionAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity ac = getActivity();
        List<Action> ls = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ?
                Action.listOf(ac, R.id.action_local_root, R.id.action_local_downloads, R.id.action_local_pictures,
                        R.id.action_local_music, R.id.action_local_video) :
                Action.listOf(ac, R.id.action_local_root, R.id.action_local_downloads, R.id.action_local_documents,
                        R.id.action_local_pictures, R.id.action_local_music, R.id.action_local_video);

        adapter = new ActionAdapter(ac, ls);
        setListAdapter(adapter);
    }

    @SuppressLint("InlinedApi")
    @Override
    void onListItemClick(int position) {
        Action action = adapter.getItem(position);
        File f = null;

        try {
            switch (action.getId()) {
            case R.id.action_local_root:
                f = Environment.getExternalStorageDirectory();
                break;

            case R.id.action_local_downloads:
                f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                break;

            case R.id.action_local_documents:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                }
                break;

            case R.id.action_local_pictures:
                f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                break;

            case R.id.action_local_music:
                f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                break;

            case R.id.action_local_video:
                f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                break;
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        if (f != null) {
            getMainActivity().getNavigation().openFolder(FragmentFolderLocal.class, new OperationFolderLocal(f));
        }
    }
}
