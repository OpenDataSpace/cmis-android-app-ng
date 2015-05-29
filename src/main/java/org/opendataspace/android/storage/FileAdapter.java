package org.opendataspace.android.storage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapter;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends DataAdapter {

    private final ArrayList<FileInfo> data = new ArrayList<>();

    public FileAdapter(Context context) {
        super(context, R.layout.delegate_node);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vw = super.getView(position, convertView, parent);
        TextView tv1 = (TextView) vw.findViewById(R.id.text_listitem_primary);
        TextView tv2 = (TextView) vw.findViewById(R.id.text_listitem_secondary);
        ImageView iv = (ImageView) vw.findViewById(R.id.action_listitem_more);
        FileInfo info = data.get(position);

        tv1.setText(info.getName(inf.getContext()));
        tv1.setCompoundDrawablesWithIntrinsicBounds(info.getIcon(context), 0, 0, 0);
        tv2.setText(info.getNodeDecription(inf.getContext()));
        iv.setVisibility(info.isDirectory() ? View.VISIBLE : View.GONE);
        return vw;
    }

    public void update(List<FileInfo> file) {
        data.clear();
        data.addAll(file);
        invalidate();
    }
}
