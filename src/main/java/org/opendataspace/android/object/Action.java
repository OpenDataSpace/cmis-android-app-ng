package org.opendataspace.android.object;

import android.content.Context;

import org.opendataspace.android.app.beta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Action {

    private final int id;
    private final String name;
    private final int iconId;

    private Action(int id, Context context) {
        this.id = id;

        switch (id) {
        case R.id.action_nav_manage:
            name = context.getString(R.string.nav_manage);
            iconId = R.drawable.ic_gear;
            break;

        case R.id.action_nav_settings:
            name = context.getString(R.string.nav_settings);
            iconId = R.drawable.ic_settings;
            break;

        case R.id.action_nav_addaccount:
            name = context.getString(R.string.nav_addaccount);
            iconId = R.drawable.ic_add;
            break;

        case R.id.action_nav_localfolder:
            name = context.getString(R.string.nav_localfolder);
            iconId = R.drawable.ic_folder;
            break;

        case R.id.action_local_root:
            name = context.getString(R.string.folder_root);
            iconId = R.drawable.ic_phone;
            break;

        case R.id.action_local_downloads:
            name = context.getString(R.string.folder_downloads);
            iconId = R.drawable.ic_downloads;
            break;

        case R.id.action_local_documents:
            name = context.getString(R.string.folder_documents);
            iconId = R.drawable.ic_documents;
            break;

        case R.id.action_local_pictures:
            name = context.getString(R.string.folder_pictures);
            iconId = R.drawable.ic_pictures;
            break;

        case R.id.action_local_music:
            name = context.getString(R.string.folder_music);
            iconId = R.drawable.ic_music;
            break;

        case R.id.action_local_video:
            name = context.getString(R.string.folder_video);
            iconId = R.drawable.ic_videos;
            break;

        default:
            name = "";
            iconId = 0;
        }
    }

    public int getIconId() {
        return iconId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<Action> listOf(Context context, int... values) {
        List<Action> ls = new ArrayList<>();

        for (int cur : values) {
            ls.add(new Action(cur, context));
        }

        return Collections.unmodifiableList(ls);
    }

    @Override
    public String toString() {
        return getName();
    }
}
