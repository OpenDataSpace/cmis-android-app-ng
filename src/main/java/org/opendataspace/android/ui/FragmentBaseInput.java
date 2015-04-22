package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.opendataspace.android.app.CompatKeyboard;
import org.opendataspace.android.app.CompatLambda;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
class FragmentBaseInput extends FragmentBase {

    private interface InputEntry {

        void apply(ActivityBase ac);

        boolean read(ActivityBase ac, Dirty dirty);

        void highlightError(ActivityBase ac);

        void init(ActivityBase ac);
    }

    private static class TextInputEntry implements InputEntry {

        private final CompatLambda.Supplier<String> getter;
        private final CompatLambda.Consumer<String> setter;
        private final CompatLambda.Predicate<String> validator;
        private final int resource;
        private final CompatLambda.Checker imeDone;

        public TextInputEntry(int resource, CompatLambda.Supplier<String> getter, CompatLambda.Consumer<String> setter,
                              CompatLambda.Predicate<String> validator, CompatLambda.Checker imeDone) {
            this.getter = getter;
            this.setter = setter;
            this.validator = validator;
            this.resource = resource;
            this.imeDone = imeDone;
        }

        @Override
        public void apply(ActivityBase ac) {
            View vw = ac.findViewById(resource);

            if (vw instanceof EditText) {
                ((EditText) vw).setText(getter.get());
            }
        }

        @Override
        public boolean read(ActivityBase ac, Dirty dirty) {
            View vw = ac.findViewById(resource);

            if (vw instanceof EditText) {
                String val = ((EditText) vw).getText().toString().trim();

                if (val.equals(getter.get())) {
                    return true;
                }

                if (validator == null || validator.test(val)) {
                    try {
                        setter.accept(val);
                        dirty.value = true;
                        return true;
                    } catch (Exception ex) {
                        OdsLog.ex(getClass(), ex);
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

        @Override
        public void init(ActivityBase ac) {
            if (imeDone != null) {
                EditText et = (EditText) ac.findViewById(resource);

                et.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (!imeDone.test()) {
                            return true; // do not hide keyboard
                        }
                    }
                    return false;
                });
            }
        }
    }

    private static class CheckInputEntry implements InputEntry {

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
        public boolean read(ActivityBase ac, Dirty dirty) {
            View vw = ac.findViewById(resource);

            if (!(vw instanceof CompoundButton)) {
                return false;
            }

            boolean val = ((CompoundButton) vw).isChecked();

            if (val == getter.get()) {
                return true;
            }

            try {
                setter.accept(val);
                dirty.value = true;
                return true;
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }

            return false;
        }

        @Override
        public void highlightError(ActivityBase ac) {
            throw new IllegalStateException();
        }

        @Override
        public void init(ActivityBase ac) {
            // nothing
        }
    }

    private static class Dirty {
        public boolean value = false;
    }

    private final ArrayList<InputEntry> entries = new ArrayList<>();
    private Dirty dirty = new Dirty();

    void addText(int resource, CompatLambda.Supplier<String> getter, CompatLambda.Consumer<String> setter,
                 CompatLambda.Predicate<String> validator) {
        entries.add(new TextInputEntry(resource, getter, setter, validator, null));
    }

    void addText(int resource, CompatLambda.Supplier<String> getter, CompatLambda.Consumer<String> setter) {
        addText(resource, getter, setter, null);
    }

    void addBool(int resource, CompatLambda.Supplier<Boolean> getter, CompatLambda.Consumer<Boolean> setter) {
        entries.add(new CheckInputEntry(resource, getter, setter));
    }

    void apply() {
        ActivityBase ac = (ActivityBase) getActivity();

        for (InputEntry cur : entries) {
            cur.apply(ac);
        }

        dirty.value = false;
    }

    void read() {
        ActivityBase ac = (ActivityBase) getActivity();

        for (InputEntry cur : entries) {
            cur.read(ac, dirty);
        }
    }

    boolean readAndValidate() {
        ActivityBase ac = (ActivityBase) getActivity();
        boolean res = true;

        for (InputEntry cur : entries) {
            if (!cur.read(ac, dirty) && res) {
                res = false;
                cur.highlightError(ac);
            }
        }

        return res;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActivityBase ac = (ActivityBase) getActivity();

        for (InputEntry cur : entries) {
            cur.init(ac);
        }

        apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        read();
    }

    void addImeDone(int resource, CompatLambda.Supplier<String> getter, CompatLambda.Consumer<String> setter,
                    CompatLambda.Predicate<String> validator, CompatLambda.Checker action) {
        entries.add(new TextInputEntry(resource, getter, setter, validator, action));
    }

    public boolean isDirty() {
        return dirty.value;
    }
}
