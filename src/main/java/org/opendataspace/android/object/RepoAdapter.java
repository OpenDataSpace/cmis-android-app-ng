package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventDaoRepo;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

import java.util.Collections;
import java.util.List;

public class RepoAdapter extends ViewAdapter<Repo> {

    public RepoAdapter(ViewBase<Repo> view, Context context) {
        super(view, context, R.layout.delegate_list_item1);
    }

    @Override
    public void updateView(Repo item, View view) {
        TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        tw1.setText(item.getDisplayName(inf.getContext()));
        tw1.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoRepo event) {
        invalidate();
    }

    @Override
    protected void sort(List<Repo> data) {
        Collections.sort(data, (r1, r2) -> {
            int res = Integer.valueOf(r1.getType().ordinal()).compareTo(r2.getType().ordinal());
            return res != 0 ? res : r1.getName().compareTo(r2.getName());
        });
    }
}
