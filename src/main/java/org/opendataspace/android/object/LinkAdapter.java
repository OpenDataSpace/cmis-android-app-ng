package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.view.ViewAdapter;
import org.opendataspace.android.view.ViewBase;

public class LinkAdapter extends ViewAdapter<Link> {

    private final View.OnClickListener more;

    protected LinkAdapter(final ViewBase<Link> view, final Context context, final View.OnClickListener more) {
        super(view, context, R.layout.delegate_link);
        this.more = more;
    }

    @Override
    protected void updateView(final Link item, final View view) {
        final TextView tw1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        final TextView tw2 = (TextView) view.findViewById(R.id.text_listitem_secondary);
        final ImageButton ib = (ImageButton) view.findViewById(R.id.action_listitem_more);

        view.setTag(R.id.internal_more, item);
        tw1.setText(item.getName());
        tw2.setText(item.getUrl());
        ib.setOnClickListener(more);
    }
}
