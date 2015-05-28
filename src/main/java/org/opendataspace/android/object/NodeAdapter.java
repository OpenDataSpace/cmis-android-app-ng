package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

import java.util.Collections;
import java.util.List;

public class NodeAdapter extends ViewAdapter<Node> {

    private final View.OnClickListener more;

    public NodeAdapter(ViewBase<Node> view, Context context, View.OnClickListener more) {
        super(view, context, R.layout.delegate_node);
        this.more = more;
    }

    @Override
    public void updateView(Node item, View view) {
        TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        TextView tw2 = (TextView) view.findViewById(R.id.text_listitem_secondary);
        ImageButton ib = (ImageButton) view.findViewById(R.id.action_listitem_more);

        view.setTag(R.id.internal_more, item);
        tw1.setText(item.getName());
        tw1.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(context), 0, 0, 0);
        tw2.setText(item.getNodeDecription(context));
        ib.setVisibility(item.getType() == Node.Type.FOLDER ? View.VISIBLE : View.GONE);
        ib.setOnClickListener(more);
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

    public Node resolve(ViewParent view) {
        if (!(view instanceof View))
            return null;

        Object res = ((View) view).getTag(R.id.internal_more);
        return  res instanceof Node ? (Node) res : null;
    }
}
