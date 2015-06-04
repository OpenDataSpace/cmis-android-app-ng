package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opendataspace.android.app.beta.R;

@SuppressLint("ValidFragment")
class FragmentBaseList extends FragmentBase {

    final private Handler handler = new Handler();

    final private Runnable requestFocus = new Runnable() {
        public void run() {
            list.focusableViewAvailable(list);
        }
    };

    final private AdapterView.OnItemClickListener clickListener =
            (parent, v, position, id) -> onListItemClick(position);

    final private AdapterView.OnItemLongClickListener longClickListener =
            (parent, v, position, id) -> onListItemLongClick(position);

    private ListAdapter adapter;
    private ListView list;
    private View emptyView;
    private TextView standardEmptyView;
    private View progressContainer;
    private View listContainer;
    private CharSequence emptyText;
    private boolean isListShown;

    public FragmentBaseList() {
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a ListView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     * <p>
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);

        // ------------------------------------------------------------------

        LinearLayout pframe = new LinearLayout(context);
        pframe.setId(R.id.internal_progress);
        pframe.setOrientation(LinearLayout.VERTICAL);
        pframe.setVisibility(View.GONE);
        pframe.setGravity(Gravity.CENTER);

        ProgressBar progress = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        pframe.addView(progress,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView pw = new TextView(context);
        pw.setText(getString(R.string.common_pleasewait));
        pframe.addView(pw,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(pframe,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ------------------------------------------------------------------

        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(R.id.internal_listcontainer);

        TextView tv = new TextView(getActivity());
        tv.setId(R.id.internal_empty);
        tv.setGravity(Gravity.CENTER);
        lframe.addView(tv,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ListView lv = new ListView(getActivity());
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        lframe.addView(lv,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.addView(lframe,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ------------------------------------------------------------------

        root.setLayoutParams(
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureList();
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        handler.removeCallbacks(requestFocus);
        list = null;
        isListShown = false;
        emptyView = progressContainer = listContainer = null;
        standardEmptyView = null;
        super.onDestroyView();
    }

    /**
     * This method will be called when an item in the list is ods_selectable.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the
     * data associated with the ods_selectable item.
     *
     * @param position The position of the view in the list
     */
    void onListItemClick(int position) {
    }

    /**
     * Provide the cursor for the list view.
     */
    void setListAdapter(ListAdapter adapter) {
        boolean hadAdapter = this.adapter != null;
        this.adapter = adapter;
        if (list != null) {
            list.setAdapter(adapter);
            if (!isListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                View v = getView();
                setListShown(true, v != null && v.getWindowToken() != null);
            }
        }
    }

    /**
     * The default content for a ListFragment has a TextView that can
     * be shown when the list is empty.  If you would like to have it
     * shown, call this method to supply the text it should use.
     */
    void setEmptyText(CharSequence text) {
        ensureList();
        if (standardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        standardEmptyView.setText(text);
        if (emptyText == null) {
            list.setEmptyView(standardEmptyView);
        }
        emptyText = text;
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the list view is shown; if false, the progress
     *                indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     *                new state.
     */
    void setListShown(boolean shown, boolean animate) {
        ensureList();
        if (progressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (isListShown == shown) {
            return;
        }
        isListShown = shown;
        if (shown) {
            if (animate) {
                progressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                progressContainer.clearAnimation();
                listContainer.clearAnimation();
            }
            progressContainer.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                progressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                progressContainer.clearAnimation();
                listContainer.clearAnimation();
            }
            progressContainer.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.GONE);
        }
    }

    private void ensureList() {
        if (list != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ListView) {
            list = (ListView) root;
        } else {
            standardEmptyView = widget(R.id.internal_empty);
            if (standardEmptyView == null) {
                emptyView = widget(android.R.id.empty);
            } else {
                standardEmptyView.setVisibility(View.GONE);
            }
            progressContainer = widget(R.id.internal_progress);
            listContainer = widget(R.id.internal_listcontainer);
            View rawListView = widget(android.R.id.list);
            if (!(rawListView instanceof ListView)) {
                if (rawListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ListView whose id attribute is " + "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' " + "that is not a ListView class");
            }
            list = (ListView) rawListView;
            if (emptyView != null) {
                list.setEmptyView(emptyView);
            } else if (emptyText != null) {
                standardEmptyView.setText(emptyText);
                list.setEmptyView(standardEmptyView);
            }
        }
        isListShown = true;
        list.setOnItemClickListener(clickListener);
        list.setOnItemLongClickListener(longClickListener);
        if (adapter != null) {
            ListAdapter adapter = this.adapter;
            this.adapter = null;
            setListAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (progressContainer != null) {
                setListShown(false, false);
            }
        }
        handler.post(requestFocus);
    }

    public ListView getList() {
        return list;
    }

    boolean onListItemLongClick(int position) {
        return false;
    }
}
