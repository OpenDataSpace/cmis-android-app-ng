package org.opendataspace.android.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.opendataspace.android.app.CompatKeyboard;
import org.opendataspace.android.app.CompatLambda;
import org.opendataspace.android.app.beta.R;

import java.util.ArrayList;

public class FragmentBaseInput extends FragmentBase {

    private interface InputEntry {

        public void apply(ActivityBase ac);

        public boolean read(ActivityBase ac);

        public void highlightError(ActivityBase ac);
    }

    private final class TextInputEntry implements InputEntry {

        private final CompatLambda.Supplier<String> getter;
        private final CompatLambda.Consumer<String> setter;
        private final CompatLambda.Predicate<String> validator;
        private final int resource;

        public TextInputEntry(int resource, CompatLambda.Supplier<String> getter, CompatLambda.Consumer<String> setter,
                              CompatLambda.Predicate<String> validator) {
            this.getter = getter;
            this.setter = setter;
            this.validator = validator;
            this.resource = resource;
        }

        @Override
        public void apply(ActivityBase ac) {
            View vw = ac.findViewById(resource);

            if (vw instanceof EditText) {
                ((EditText) vw).setText(getter.get());
            }
        }

        @Override
        public boolean read(ActivityBase ac) {
            View vw = ac.findViewById(resource);

            if (vw instanceof EditText) {
                String val = ((EditText) vw).getText().toString().trim();

                if (validator == null || validator.test(val)) {
                    try {
                        setter.accept(val);
                        return true;
                    } catch (Exception ex) {
                        Log.w(getClass().getSimpleName(), ex);
                    }
                }
            }

            return false;
        }

        @Override
        public void highlightError(ActivityBase ac) {
            EditText et = (EditText) ac.findViewById(resource);
            CompatKeyboard.request(et, ac);
            et.selectAll();
            ac.showToast(R.string.common_invalidvalue);
        }
    }

    private final class CheckInputEntry implements InputEntry {

        private final CompatLambda.Supplier<Boolean> getter;
        private final CompatLambda.Consumer<Boolean> setter;
        private final int resource;

        public CheckInputEntry(int resource, CompatLambda.Supplier<Boolean> getter,
                               CompatLambda.Consumer<Boolean> setter) {
            this.getter = getter;
            this.setter = setter;
            this.resource = resource;
        }

        @Override
        public void apply(ActivityBase ac) {
            View vw = ac.findViewById(resource);

            if (vw instanceof CompoundButton) {
                ((CompoundButton) vw).setChecked(getter.get());
            }
        }

        @Override
        public boolean read(ActivityBase ac) {
            View vw = ac.findViewById(resource);

            if (vw instanceof CompoundButton) {
                try {
                    setter.accept(((CompoundButton) vw).isChecked());
                    return true;
                } catch (Exception ex) {
                    Log.w(getClass().getSimpleName(), ex);
                }
            }

            return false;
        }

        @Override
        public void highlightError(ActivityBase ac) {
            throw new IllegalStateException();
        }
    }

    private ArrayList<InputEntry> entries = new ArrayList<>();

    protected FragmentBaseInput addText(int resource, CompatLambda.Supplier<String> getter,
                                        CompatLambda.Consumer<String> setter,
                                        CompatLambda.Predicate<String> validator) {
        entries.add(new TextInputEntry(resource, getter, setter, validator));
        return this;
    }

    protected FragmentBaseInput addText(int resource, CompatLambda.Supplier<String> getter,
                                        CompatLambda.Consumer<String> setter) {
        return addText(resource, getter, setter, null);
    }

    protected FragmentBaseInput addBool(int resource, CompatLambda.Supplier<Boolean> getter,
                                        CompatLambda.Consumer<Boolean> setter) {
        entries.add(new CheckInputEntry(resource, getter, setter));
        return this;
    }

    protected void apply() {
        ActivityBase ac = (ActivityBase) getActivity();

        for (InputEntry cur : entries) {
            cur.apply(ac);
        }
    }

    protected void read() {
        ActivityBase ac = (ActivityBase) getActivity();

        for (InputEntry cur : entries) {
            cur.read(ac);
        }
    }

    protected boolean readAndValidate() {
        ActivityBase ac = (ActivityBase) getActivity();
        boolean res = true;

        for (InputEntry cur : entries) {
            if (!cur.read(ac) && res) {
                res = false;
                cur.highlightError(ac);
            }
        }

        return res;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        read();
    }
}
