package org.opendataspace.android.data;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataAdapterMerge extends BaseAdapter {

    private final List<Adapter> list = new ArrayList<>();

    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };

    public void addAdapter(Adapter adapter) {
        list.add(adapter);
        adapter.registerDataSetObserver(observer);
    }

    public void addViews(View... views) {
        addAdapter(new DataAdapterViews(views));
    }

    @Override
    public int getCount() {
        int cnt = 0;

        for (Adapter cur : list) {
            cnt += cur.getCount();
        }

        return cnt;
    }

    @Override
    public Object getItem(int i) {
        for (Adapter cur : list) {
            int size = cur.getCount();

            if (i < size) {
                return cur.getItem(i);
            }

            i -= size;
        }

        return null;
    }

    @Override
    public long getItemId(int i) {
        for (Adapter cur : list) {
            int size = cur.getCount();

            if (i < size) {
                return cur.getItemId(i);
            }

            i -= size;
        }

        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        for (Adapter cur : list) {
            int size = cur.getCount();

            if (i < size) {
                return cur.getView(i, view, viewGroup);
            }

            i -= size;
        }

        return null;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        for (Adapter cur : list) {
            int size = cur.getCount();

            if (i < size) {
                return cur instanceof SpinnerAdapter ? ((SpinnerAdapter) cur).getDropDownView(i, view, viewGroup) :
                        cur.getView(i, view, viewGroup);
            }

            i -= size;
        }

        return null;
    }
}
