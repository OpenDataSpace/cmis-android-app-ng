package org.opendataspace.android.object;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;

import java.util.List;

public class ActionAdapter extends ArrayAdapter<Action> {

    public ActionAdapter(Context context, List<Action> objects, int layout) {
        super(context, layout, R.id.text_listitem_primary, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vw = super.getView(position, convertView, parent);
        TextView tv1 = (TextView) vw.findViewById(R.id.text_listitem_primary);
        Action ac = getItem(position);
        tv1.setCompoundDrawablesWithIntrinsicBounds(ac.getIconId(), 0, 0, 0);
        return vw;
    }
}
