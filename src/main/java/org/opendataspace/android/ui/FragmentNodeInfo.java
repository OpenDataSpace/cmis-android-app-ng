package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationNodeBrowse;

@SuppressLint("ValidFragment")
public class FragmentNodeInfo extends FragmentBase {

    private final OperationNodeBrowse op;

    public FragmentNodeInfo(OperationNodeBrowse op) {
        this.op = op;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nodeinfo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Node node = op.getNode();
        Activity ac = getActivity();

        this.<TextView>widget(R.id.text_node_title).setText(node.getName());
        this.<TextView>widget(R.id.text_node_details).setText(node.getDecription(ac));
        this.<TextView>widget(R.id.text_node_name).setText(node.getName());
        this.<TextView>widget(R.id.text_node_path).setText(node.getPath(ac));
        this.<TextView>widget(R.id.text_node_created).setText(node.getCreatedAt(ac));
        this.<TextView>widget(R.id.text_node_creator).setText(node.getCreatedBy());
        this.<TextView>widget(R.id.text_node_modified).setText(node.getModifiedAt(ac));
        this.<TextView>widget(R.id.text_node_modifier).setText(node.getModifiedBy());
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.node_title);
    }
}
