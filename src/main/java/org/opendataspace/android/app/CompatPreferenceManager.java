package org.opendataspace.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CompatPreferenceManager {

    private static final String TAG = CompatPreferenceManager.class.getSimpleName();

    /**
     * Interface definition for a callback to be invoked when a
     * {@link Preference} in the hierarchy rooted at this {@link PreferenceScreen} is
     * clicked.
     */
    public interface OnPreferenceTreeClickListener {
        /**
         * Called when a preference in the tree rooted at this
         * {@link PreferenceScreen} has been clicked.
         *
         * @param preference The preference that was clicked.
         * @return Whether the click was handled.
         */
        boolean onPreferenceTreeClick(Preference preference);
    }

    public static PreferenceManager newInstance(Activity activity, int firstRequestCode) {
        try {
            Constructor<PreferenceManager> c =
                    PreferenceManager.class.getDeclaredConstructor(Activity.class, int.class);
            c.setAccessible(true);
            return c.newInstance(activity, firstRequestCode);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
        return null;
    }

    /**
     * Sets the callback to be invoked when a {@link Preference} in the
     * hierarchy rooted at this {@link PreferenceManager} is clicked.
     *
     * @param listener The callback to be invoked.
     */
    public static void setOnPreferenceTreeClickListener(PreferenceManager manager,
                                                        final OnPreferenceTreeClickListener listener) {
        try {
            Field onPreferenceTreeClickListener =
                    PreferenceManager.class.getDeclaredField("mOnPreferenceTreeClickListener");
            onPreferenceTreeClickListener.setAccessible(true);
            if (listener != null) {
                Object proxy = Proxy.newProxyInstance(onPreferenceTreeClickListener.getType().getClassLoader(),
                        new Class[] {onPreferenceTreeClickListener.getType()}, (proxy1, method, args) -> {
                            if (method.getName().equals("onPreferenceTreeClick")) {
                                return listener.onPreferenceTreeClick((Preference) args[1]);
                            } else {
                                return null;
                            }
                        });
                onPreferenceTreeClickListener.set(manager, proxy);
            } else {
                onPreferenceTreeClickListener.set(manager, null);
            }
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
    }

    /**
     * Inflates a preference hierarchy from XML. If a preference hierarchy is
     * given, the new preference hierarchies will be merged in.
     *
     * @param resId The resource ID of the XML to inflate.
     * @return The root hierarchy (if one was not provided, the new hierarchy's
     * root).
     */
    public static PreferenceScreen inflateFromResource(PreferenceManager manager, Activity activity, int resId,
                                                       PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class
                    .getDeclaredMethod("inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, activity, resId, screen);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
        return null;
    }

    /**
     * Returns the root of the preference hierarchy managed by this class.
     *
     * @return The {@link PreferenceScreen} object that is at the root of the hierarchy.
     */
    public static PreferenceScreen getPreferenceScreen(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen");
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
        return null;
    }

    /**
     * Called by the {@link PreferenceManager} to dispatch a sub-activity result.
     */
    public static void dispatchActivityResult(PreferenceManager manager, int requestCode, int resultCode, Intent data) {
        try {
            Method m = PreferenceManager.class
                    .getDeclaredMethod("dispatchActivityResult", int.class, int.class, Intent.class);
            m.setAccessible(true);
            m.invoke(manager, requestCode, resultCode, data);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
    }

    /**
     * Called by the {@link PreferenceManager} to dispatch the activity stop
     * event.
     */
    public static void dispatchActivityStop(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop");
            m.setAccessible(true);
            m.invoke(manager);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
    }

    /**
     * Called by the {@link PreferenceManager} to dispatch the activity destroy
     * event.
     */
    public static void dispatchActivityDestroy(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy");
            m.setAccessible(true);
            m.invoke(manager);
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
    }

    /**
     * Sets the root of the preference hierarchy.
     *
     * @param screen The root {@link PreferenceScreen} of the preference hierarchy.
     * @return Whether the {@link PreferenceScreen} given is different than the previous.
     */
    public static boolean setPreferences(PreferenceManager manager, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", PreferenceScreen.class);
            m.setAccessible(true);
            return ((Boolean) m.invoke(manager, screen));
        } catch (Exception ex) {
            OdsLog.ex(CompatPreferenceManager.class, ex);
        }
        return false;
    }

}
