package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

import java.util.Collections;
import java.util.List;

public class NodeAdapter extends ViewAdapter<Node> {

    public NodeAdapter(ViewBase<Node> view, Context context) {
        super(view, context, R.layout.delegate_list_item2);
    }

    @Override
    public void updateView(Node item, View view) {
        TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        TextView tw2 = (TextView) view.findViewById(R.id.text_listitem_secondary);

        tw1.setText(item.getName());
        tw1.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(context), 0, 0, 0);
        tw2.setText(item.getNodeDecription(context));
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoNode event) {
        invalidate();
    }

    @Override
    protected void sort(List<Node> data) {
        Collections.sort(data, (n1, n2) -> {
            int res = Boolean.valueOf(n1.getType() == Node.Type.FOLDER).compareTo(n2.getType() == Node.Type.FOLDER);
            return res != 0 ? -res : n1.getName().compareToIgnoreCase(n2.getName());
        });
    }
}
