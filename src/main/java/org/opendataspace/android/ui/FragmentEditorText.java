package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationLoadText;

@SuppressLint("ValidFragment")
public class FragmentEditorText extends FragmentBase {

    private static final int REQUEST_SPEECH = 0;

    private final OperationLoadText op;
    private boolean hasSpeech;

    public FragmentEditorText(final OperationLoadText op) {
        this.op = op;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor_text, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity ac = getActivity();
        hasSpeech = !ac.getPackageManager().queryIntentActivities(createSpeechIntent(), 0).isEmpty();
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentEditorText::textAvailable)).start();
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_editor_text;
    }

    @Override
    public String getTile(final Context context) {
        return op.getFile() == null ? context.getString(R.string.app_editor_text) : op.getFile().getName();
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final MenuItem mi = menu.findItem(R.id.menu_editor_speech);

        if (mi != null) {
            mi.setVisible(hasSpeech);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_editor_save:
            actionSave();
            break;

        case R.id.menu_editor_speech:
            actionSpeech();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionSave() {
    }

    private void actionSpeech() {
        try {
            startActivityForResult(createSpeechIntent(), REQUEST_SPEECH);
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    private Intent createSpeechIntent() {
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        return intent;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH && resultCode == Activity.RESULT_OK && data != null) {
            final String joined = TextUtils.join("\n", data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));

            if (TextUtils.isEmpty(joined)) {
                return;
            }

            final TextView tv = widget(R.id.editor_edit_main);
            final int start = tv.getSelectionStart();
            final int end = tv.getSelectionEnd();
            ((Editable) tv.getText()).replace(Math.min(start, end), Math.max(start, end), joined, 0, joined.length());
        }
    }

    private void textAvailable(final OperationLoadText op) {
        this.<EditText>widget(R.id.editor_edit_main).setText(op.getText());
        getNavigation().updateTitle();
    }


}
