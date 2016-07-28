package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationLoadText;

public class ActivityEditorText extends ActivityDialog {

    @Override
    protected void onInit(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_dialog);
        setSupportActionBar((Toolbar) findViewById(R.id.dialog_view_toolbar));
        applyFragmment(new FragmentEditorText(new OperationLoadText(getIntent().getData().toString())));
    }
}
