package org.opendataspace.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opendataspace.android.app.beta.R;

public class FragmentAccountDetails extends FragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.account_title);
    }
}
