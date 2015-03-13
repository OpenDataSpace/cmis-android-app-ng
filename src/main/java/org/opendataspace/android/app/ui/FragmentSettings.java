package org.opendataspace.android.app.ui;

import android.os.Bundle;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import org.opendataspace.android.app.beta.R;

public class FragmentSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
