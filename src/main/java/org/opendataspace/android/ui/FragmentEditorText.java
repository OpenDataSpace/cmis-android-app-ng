package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.opendataspace.android.app.CompatDeprecated;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationLoadText;

import java.io.FileOutputStream;
import java.io.OutputStream;

@SuppressLint("ValidFragment")
public class FragmentEditorText extends FragmentBase {

    private static final int REQUEST_SPEECH = 0;

    private final OperationLoadText op;
    private boolean hasSpeech;
    private boolean hasChanges;

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

        this.<ScrollView>widget(R.id.editor_view_scroll).setSmoothScrollingEnabled(true);
        this.<EditText>widget(R.id.editor_edit_main).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                hasChanges = true;
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });

        final Activity ac = getActivity();
        hasSpeech = !ac.getPackageManager().queryIntentActivities(createSpeechIntent(), 0).isEmpty();
        actionReload();
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

        case R.id.menu_editor_reload:
            actionReload();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionSave() {
        if (op.getFile() == null) {
            return;
        }

        OutputStream sourceFile = null;

        try {
            sourceFile = new FileOutputStream(op.getFile());
            sourceFile.write(this.<EditText>widget(R.id.editor_edit_main).getText().toString().getBytes("UTF-8"));
            hasChanges = false;
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        } finally {
            IOUtils.closeQuietly(sourceFile);
        }
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
        final ActivityBase ac = (ActivityBase) getActivity();
        ac.stopWait();

        if (op.reportError(ac)) {
            return;
        }

        this.<EditText>widget(R.id.editor_edit_main).setText(op.getText());
        hasChanges = false;
        getNavigation().updateTitle();
    }

    @Override
    public boolean backPressed() {
        if (hasChanges && op.getFile() != null) {
            requestSave();
            return true;
        }

        return false;
    }

    private void requestSave() {
        final Activity ac = getActivity();

        new AlertDialog.Builder(ac).setTitle(R.string.editor_modified)
                .setMessage(CompatDeprecated.fromHtml(getString(R.string.editor_quit)))
                .setPositiveButton(R.string.editor_save, (dialog, item) -> {
                    dialog.dismiss();
                    actionSave();
                    ac.finish();
                }).
                setNegativeButton(R.string.editor_discard, (dialog, item) -> {
                    dialog.dismiss();
                    ac.finish();
                }).setNeutralButton(R.string.common_cancel, (dialog, item) -> dialog.dismiss()).create().show();
    }

    private void actionReload() {
        final ActivityBase ac = (ActivityBase) getActivity();
        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), di -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentEditorText::textAvailable)).start();
    }
}
