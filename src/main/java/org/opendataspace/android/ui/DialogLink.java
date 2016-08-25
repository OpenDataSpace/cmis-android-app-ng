package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Link;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("ValidFragment")
public class DialogLink extends DialogFragment {

    private final Link link;
    private final boolean isEdit;
    private EditText tvn, tve, tvp, tvm;
    private TextView dpe;
    private final Runnable onOk;

    private static abstract class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
    }

    public DialogLink(final Link link, final boolean isEdit, final Runnable onOk) {
        this.link = link;
        this.isEdit = isEdit;
        this.onOk = onOk;
    }

    @Override
    @NonNull
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity ac = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(ac);
        final View view = ac.getLayoutInflater().inflate(R.layout.dialog_link, null);

        tvn = (EditText) view.findViewById(R.id.link_name);
        tve = (EditText) view.findViewById(R.id.link_email);
        tvp = (EditText) view.findViewById(R.id.link_password);
        tvm = (EditText) view.findViewById(R.id.link_message);
        dpe = (TextView) view.findViewById(R.id.link_expires);

        tvn.setText(link.getName());
        tve.setText(link.getEmail());
        tvp.setText(link.getPassword());
        tvm.setText(link.getMessage());
        dpe.setText(SimpleDateFormat.getDateInstance().format(link.getExpires().getTime()));

        if (isEdit) {
            tvn.setEnabled(false);
            tve.setEnabled(false);
            tvm.setEnabled(false);
        }

        dpe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment() {

                    @NonNull
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        final Calendar exp = link.getExpires();
                        return new DatePickerDialog(getActivity(), this, exp.get(Calendar.YEAR),
                                exp.get(Calendar.MONTH), exp.get(Calendar.DAY_OF_MONTH));
                    }

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                        if (cal.before(Calendar.getInstance())) {
                            return;
                        }

                        link.setExpires(cal);
                        dpe.setText(SimpleDateFormat.getDateInstance().format(link.getExpires().getTime()));
                    }
                };

                newFragment.show(getFragmentManager(), "timePicker");
            }
        });


        builder.setView(view);
        builder.setNegativeButton(getString(R.string.common_cancel), (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton(getString(R.string.common_ok), (dialog, which) -> {
            if (!validate(link)) {
                return;
            }

            onOk.run();
            dismiss();
        });

        return builder.create();
    }

    private boolean validate(Link link) {
        link.setEmail(tve.getText().toString().trim());
        link.setMessage(tvm.getText().toString().trim());
        link.setName(tvn.getText().toString().trim());
        link.setPassword(tvp.getText().toString().trim());

        return link.isValid();
    }
}
