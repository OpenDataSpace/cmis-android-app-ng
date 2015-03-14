package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.opendataspace.android.app.OdsApplication;
import org.opendataspace.android.app.beta.R;

import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public class FragmentAbout extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tva = (TextView) getActivity().findViewById(R.id.text_about_info);
        tva.setMovementMethod(new LinkMovementMethod());

        try {
            tva.setText(Html.fromHtml(
                    String.format(getString(R.string.about_text_info), OdsApplication.get().getPrefs().version(),
                            Calendar.getInstance().get(Calendar.YEAR))));
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }
    }
}
