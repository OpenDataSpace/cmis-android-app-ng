package org.opendataspace.android.storage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapter;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends DataAdapter {

    private final ArrayList<FileInfo> data = new ArrayList<>();

    public FileAdapter(Context context) {
        super(context, R.layout.delegate_list_item1);
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
        TextView tv = (TextView) vw.findViewById(R.id.text_listitem_primary);
        FileInfo info = data.get(position);
        tv.setText(info.getName());
        tv.setCompoundDrawablesWithIntrinsicBounds(info.getIcon(context), 0, 0, 0);
        return vw;
    }

    public void update(List<FileInfo> file) {
        data.clear();
        data.addAll(file);
        invalidate();
    }
}
