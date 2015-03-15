package org.opendataspace.android.ui;

import android.os.Bundle;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.settings.PreferenceFragment;

@SuppressWarnings("WeakerAccess")
public class FragmentSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
