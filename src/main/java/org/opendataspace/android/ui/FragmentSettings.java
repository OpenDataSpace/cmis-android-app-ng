package org.opendataspace.android.ui;

import android.os.Bundle;

import org.opendataspace.android.app.beta.R;

public class FragmentSettings extends FragmentBasePreference {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
