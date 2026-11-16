package app.morphe.extension.syncextras;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * GPLv3 §7(b) attribution required by Patcheddit's NOTICE for the Redgifs patches,
 * which are derived from Patcheddit code. Shown as a toast on the first launch
 * after each install or update of the patched app.
 *
 * @noinspection unused
 */
public final class Attribution {
    private static final String MESSAGE =
            "This app uses code from Patcheddit. To learn more, visit https://reddit.com/r/patcheddit";
    private static final String PREFS = "syncextras_attribution";
    private static final String KEY = "shown_for_install";

    private Attribution() {
    }

    public static void show(Context context) {
        try {
            final Context app = context.getApplicationContext() != null
                    ? context.getApplicationContext() : context;
            PackageInfo info = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            long install = info.lastUpdateTime;

            SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (prefs.getLong(KEY, 0L) == install) return;
            prefs.edit().putLong(KEY, install).apply();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(app, MESSAGE, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Throwable ignored) {
            // Never let attribution crash app startup.
        }
    }
}
