package org.opendataspace.android.storage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.data.DataAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FileAdapter extends DataAdapter {

    private ArrayList<File> data = new ArrayList<>();

    public FileAdapter(Context context, File root) {
        super(context, R.layout.delegate_list_item1);
        addAll(root);
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
        File file = data.get(position);
        tv.setText(file.getName());
        tv.setCompoundDrawablesWithIntrinsicBounds(file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file, 0, 0,
                0);

        return vw;
    }

    public void update(File file) {
        data.clear();
        addAll(file);
        invalidate();
    }

    private void addAll(File file) {
        if (file == null) {
            return;
        }

        File[] ls = file.listFiles(cur -> !cur.isHidden());

        if (ls == null) {
            return;
        }

        data.addAll(Arrays.asList(ls));

        Collections.sort(data, (f1, f2) -> {
            int res = Boolean.valueOf(f1.isDirectory()).compareTo(f2.isDirectory());
            return res != 0 ? -res : f1.getName().compareToIgnoreCase(f2.getName());
        });
    }
}
