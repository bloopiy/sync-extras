package app.morphe.extension.syncextras;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Reads the on/off switches for Sync Extras patches.
 * <p>
 * When wchill/patcheddit's Morphe settings menu is in the APK, the Sync Extras
 * patches add their switches to it, and that menu stores them in the app's default
 * SharedPreferences file (<package>_preferences). This class reads the same file by
 * name, with plain Android APIs, so it never links against wchill's code. Without
 * wchill's menu nothing writes these keys and every patch keeps its default.
 *
 * @noinspection unused
 */
public final class SyncExtrasSettings {
    private static final String TAG = "SyncExtras";

    // Keys are prefixed morphe_patch_ so wchill's menu shows its "restart the app"
    // toast when they change, and namespaced syncextras_ so they never collide with
    // wchill's own keys. Must match Settings.kt.
    public static final String UNLOCK_SYNC_ULTRA = "morphe_patch_syncextras_unlock_sync_ultra";
    public static final String REDGIFS_WEBVIEW_FALLBACK = "morphe_patch_syncextras_redgifs_webview_fallback";

    private static volatile Context context;

    private SyncExtrasSettings() {
    }

    /** Called from RedditApplication.onCreate, before anything reads a switch. */
    public static void initialize(Context appContext) {
        if (appContext == null) return;
        Context app = appContext.getApplicationContext();
        context = app != null ? app : appContext;
    }

    public static boolean isEnabled(String key, boolean defaultValue) {
        Context current = context;
        if (current == null) return defaultValue;
        try {
            SharedPreferences preferences = current.getSharedPreferences(
                    current.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            return preferences.getBoolean(key, defaultValue);
        } catch (Throwable error) {
            Log.w(TAG, "Could not read setting " + key + "; using default.", error);
            return defaultValue;
        }
    }

    public static boolean isUltraUnlocked() {
        return isEnabled(UNLOCK_SYNC_ULTRA, true);
    }

    public static boolean isRedgifsWebViewFallbackEnabled() {
        return isEnabled(REDGIFS_WEBVIEW_FALLBACK, true);
    }
}
