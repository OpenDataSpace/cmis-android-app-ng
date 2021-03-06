package org.opendataspace.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ListView;

import org.opendataspace.android.app.CompatPreferenceManager;
import org.opendataspace.android.app.beta.R;

import java.lang.ref.WeakReference;

abstract class FragmentBasePreference extends FragmentBase
        implements CompatPreferenceManager.OnPreferenceTreeClickListener {

    private static final String PREFERENCES_TAG = "android:preferences";

    private PreferenceManager preferenceManager;
    private ListView list;
    private boolean havePrefs;
    private boolean initDone;

    private static class PrefHandler extends Handler {

        private final WeakReference<FragmentBasePreference> frag;

        public PrefHandler(FragmentBasePreference frag) {
            this.frag = new WeakReference<>(frag);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case MSG_BIND_PREFERENCES: {
                FragmentBasePreference f = frag.get();

                if (f != null) {
                    f.bindPreferences();
                }
            }
            break;
            }
        }
    }

    /**
     * The starting request code given out to preference framework.
     */
    private static final int FIRST_REQUEST_CODE = 100;

    private static final int MSG_BIND_PREFERENCES = 1;
    private final Handler mHandler = new PrefHandler(this);

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            list.focusableViewAvailable(list);
        }
    };

    /**
     * Interface that PreferenceFragment's containing activity should
     * implement to be able to process preference items that wish to
     * switch to a new fragment.
     */
    public interface OnPreferenceStartFragmentCallback {
        /**
         * Called when the user has clicked on a Preference that has
         * a fragment class name associated with it.  The implementation
         * to should instantiate and switch to an instance of the given
         * fragment.
         */
        boolean onPreferenceStartFragment(FragmentBasePreference caller, Preference pref);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        preferenceManager = CompatPreferenceManager.newInstance(getActivity(), FIRST_REQUEST_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return paramLayoutInflater.inflate(R.layout.fragment_preference, paramViewGroup, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (havePrefs) {
            bindPreferences();
        }

        initDone = true;

        if (savedInstanceState != null) {
            Bundle container = savedInstanceState.getBundle(PREFERENCES_TAG);
            if (container != null) {
                final PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(container);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CompatPreferenceManager.setOnPreferenceTreeClickListener(preferenceManager, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CompatPreferenceManager.dispatchActivityStop(preferenceManager);
        CompatPreferenceManager.setOnPreferenceTreeClickListener(preferenceManager, null);
    }

    @Override
    public void onDestroyView() {
        list = null;
        mHandler.removeCallbacks(mRequestFocus);
        mHandler.removeMessages(MSG_BIND_PREFERENCES);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CompatPreferenceManager.dispatchActivityDestroy(preferenceManager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CompatPreferenceManager.dispatchActivityResult(preferenceManager, requestCode, resultCode, data);
    }

    /**
     * Sets the root of the preference hierarchy that this fragment is showing.
     *
     * @param preferenceScreen The root {@link PreferenceScreen} of the preference hierarchy.
     */
    private void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (CompatPreferenceManager.setPreferences(preferenceManager, preferenceScreen) && preferenceScreen != null) {
            havePrefs = true;
            if (initDone) {
                postBindPreferences();
            }
        }
    }

    /**
     * Gets the root of the preference hierarchy that this fragment is showing.
     *
     * @return The {@link PreferenceScreen} that is the root of the preference
     * hierarchy.
     */
    private PreferenceScreen getPreferenceScreen() {
        return CompatPreferenceManager.getPreferenceScreen(preferenceManager);
    }

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the current
     * preference hierarchy.
     *
     * @param preferencesResId The XML resource ID to inflate.
     */
    void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();

        setPreferenceScreen(CompatPreferenceManager
                .inflateFromResource(preferenceManager, getActivity(), preferencesResId, getPreferenceScreen()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean onPreferenceTreeClick(Preference preference) {
        //if (preference.getFragment() != null &&
        return getActivity() instanceof OnPreferenceStartFragmentCallback &&
                ((OnPreferenceStartFragmentCallback) getActivity()).onPreferenceStartFragment(this, preference);
    }

    private void requirePreferenceManager() {
        if (preferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences() {
        if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) {
            return;
        }
        mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
    }

    private void bindPreferences() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
        }
    }

    private ListView getListView() {
        ensureList();
        return list;
    }

    private void ensureList() {
        if (list != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        View rawListView = widget(android.R.id.list);
        if (rawListView == null) {
            throw new RuntimeException(
                    "Your content must have a ListView whose id attribute is " + "'android.R.id.list'");
        }
        if (!(rawListView instanceof ListView)) {
            throw new RuntimeException(
                    "Content has view with id attribute 'android.R.id.list' " + "that is not a ListView class");
        }
        list = (ListView) rawListView;
        list.setOnKeyListener(mListOnKeyListener);
        mHandler.post(mRequestFocus);
    }

    private final OnKeyListener mListOnKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Object selectedItem = list.getSelectedItem();
            if (selectedItem instanceof Preference) {
                @SuppressWarnings({"unused", "UnusedAssignment"}) View selectedView = list.getSelectedView();
                //return ((Preference)selectedItem).onKey(
                //        selectedView, keyCode, event);
                return false;
            }
            return false;
        }
    };
}
