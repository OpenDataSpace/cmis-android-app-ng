package org.opendataspace.android.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opendataspace.android.app.CompatDeprecated;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.BuildConfig;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.EventAccountConfig;
import org.opendataspace.android.operation.OperationAccountConfig;
import org.opendataspace.android.storage.Storage;

import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public class FragmentAbout extends FragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tva = widget(R.id.text_about_info);
        tva.setMovementMethod(new LinkMovementMethod());

        try {
            tva.setText(CompatDeprecated.fromHtml(
                    String.format(getString(R.string.about_info), BuildConfig.VERSION_NAME,
                            Calendar.getInstance().get(Calendar.YEAR), getString(R.string.app_mailto))));
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OdsApp.bus.register(this);
        updateBranding();
    }

    @Override
    public void onDestroyView() {
        OdsApp.bus.unregister(this);
        super.onDestroyView();
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventAccountConfig val) {
        updateBranding();
    }

    private void updateBranding() {
        try {
            Drawable d = Storage.getBrandingDrawable(getActivity(), OdsApp.get().getViewManager().getCurrentAccount(),
                    OperationAccountConfig.BRAND_LARGE);

            if (d != null) {
                TextView tva = widget(R.id.text_about_info);
                tva.setCompoundDrawables(null, d, null, null);
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }
}
