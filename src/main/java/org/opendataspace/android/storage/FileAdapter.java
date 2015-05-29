package org.opendataspace.android.storage;

import android.content.Context;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapterList;

import java.util.List;

public class FileAdapter extends DataAdapterList<FileInfo> {

    private final View.OnClickListener more;

    public FileAdapter(Context context, View.OnClickListener more) {
        super(context, R.layout.delegate_node);
        this.more = more;
    }

    @Override
    protected void updateView(FileInfo item, View view) {
        TextView tv1 = (TextView) view.findViewById(R.id.text_listitem_primary);
        TextView tv2 = (TextView) view.findViewById(R.id.text_listitem_secondary);
        ImageView iv = (ImageView) view.findViewById(R.id.action_listitem_more);

        view.setTag(R.id.internal_more, item);
        tv1.setText(item.getName(inf.getContext()));
        tv1.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(context), 0, 0, 0);
        tv2.setText(item.getNodeDecription(inf.getContext()));
        iv.setVisibility((more != null && item.isDirectory()) ? View.VISIBLE : View.GONE);
        iv.setOnClickListener(more);
    }

    public void update(List<FileInfo> data) {
        assign(data);
        invalidate();
    }

    public FileInfo resolve(ViewParent view) {
        if (!(view instanceof View)) {
            return null;
        }

        Object res = ((View) view).getTag(R.id.internal_more);
        return res instanceof FileInfo ? (FileInfo) res : null;
    }
}
