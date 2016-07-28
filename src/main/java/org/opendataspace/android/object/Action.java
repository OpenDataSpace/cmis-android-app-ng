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

        case R.id.action_nav_addaccount:
            name = context.getString(R.string.nav_addaccount);
            iconId = R.drawable.ic_add;
            break;

        case R.id.action_nav_localfolder:
            name = context.getString(R.string.nav_localfolder);
            iconId = R.drawable.ic_folder;
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

    public static List<Action> listOf(Context context, int... values) {
        List<Action> ls = new ArrayList<>();

        for (int cur : values) {
            ls.add(new Action(cur, context));
        }

        return Collections.unmodifiableList(ls);
    }

    @Override
    public String toString() {
        return name;
    }
}
