package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.event.EventProgress;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

import java.util.Collections;
import java.util.List;

public class NodeAdapter extends ViewAdapter<Node> {

    private final View.OnClickListener more;

    public NodeAdapter(final ViewBase<Node> view, final Context context, final View.OnClickListener more) {
        super(view, context, R.layout.delegate_node);
        this.more = more;
    }

    @Override
    public void updateView(final Node item, final View view) {
        final TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        final TextView tw2 = (TextView) view.findViewById(R.id.text_listitem_secondary);
        final ImageButton ib = (ImageButton) view.findViewById(R.id.action_listitem_more);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.view_listitem_progress);

        view.setTag(R.id.internal_more, item);
        tw1.setText(item.getName());
        tw1.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(context), 0, 0, 0);
        tw2.setText(item.getNodeDecription(context));
        ib.setVisibility((more != null && item.getType() == Node.Type.FOLDER) ? View.VISIBLE : View.GONE);
        ib.setOnClickListener(more);
        pb.setVisibility(item.getProgress() == 100 ? View.GONE : View.VISIBLE);
        pb.setProgress(item.getProgress());
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_ADAPTER)
    public void onEvent(final EventDaoNode event) {
        invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_ADAPTER)
    public void onEvent(final EventProgress event) {
        if (isValid(event.getNode())) {
            invalidate();
        }
    }

    @Override
    protected void sort(final List<Node> data) {
        Collections.sort(data, (n1, n2) -> {
            final int res =
                    Boolean.valueOf(n1.getType() == Node.Type.FOLDER).compareTo(n2.getType() == Node.Type.FOLDER);

            return res != 0 ? -res : n1.getName().compareToIgnoreCase(n2.getName());
        });
    }

    public Node resolve(final ViewParent view) {
        if (!(view instanceof View)) {
            return null;
        }

        final Object res = ((View) view).getTag(R.id.internal_more);
        return res instanceof Node ? (Node) res : null;
    }
}
