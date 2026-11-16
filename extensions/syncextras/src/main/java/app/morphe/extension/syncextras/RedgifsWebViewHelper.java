/*
 * Ported from KingOfPoptart/patcheddit (a fork of wchill/patcheddit), GPLv3.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms.
 */

package app.morphe.extension.syncextras;

import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.WebView;

/**
 * Helpers for the "Open Redgifs links in WebView on failure" patch: auto-accept the
 * CookieYes consent banner on redgifs pages and keep the consent cookie
 * alive across WebView opens.
 *
 * @noinspection unused
 */
public final class RedgifsWebViewHelper {
    private RedgifsWebViewHelper() {
    }

    // CookieYes accept button: current markup ([data-cky-tag="accept-button"])
    // with the legacy class (.cky-btn-accept) as fallback. The banner is built
    // asynchronously after page-finished, so poll briefly instead of firing once.
    private static final String ACCEPT_JS =
            "(function(){var n=0,t=setInterval(function(){"
            + "var b=document.querySelector('[data-cky-tag=\"accept-button\"]')"
            + "||document.querySelector('.cky-btn-accept');"
            + "if(b){b.click();clearInterval(t);}"
            + "if(++n>20)clearInterval(t);},250);})();";

    /**
     * Called from WebViewFragment$a.onPageFinished: auto-click the CookieYes
     * accept button on redgifs pages.
     */
    public static void onPageFinished(WebView view, String url) {
        if (view == null || !isRedgifs(url)) return;
        view.evaluateJavascript(ACCEPT_JS, null);
    }

    /**
     * Sync's WebViewFragment wipes ALL cookies on every fresh WebView open
     * (CookieManager.removeAllCookie() before loadUrl). Called in place of the
     * wipe: skips it for redgifs URLs so the CookieYes consent cookie survives
     * between opens, and performs it unchanged for everything else.
     */
    @SuppressWarnings("deprecation")
    public static void removeAllCookieUnlessRedgifs(CookieManager cookieManager, String url) {
        if (!isRedgifs(url)) {
            cookieManager.removeAllCookie();
        }
    }

    private static boolean isRedgifs(String url) {
        if (url == null) return false;
        String host = Uri.parse(url).getHost();
        return host != null && (host.equals("redgifs.com") || host.endsWith(".redgifs.com"));
    }
}
