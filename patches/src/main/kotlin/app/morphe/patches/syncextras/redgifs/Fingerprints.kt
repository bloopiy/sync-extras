/*
 * Ported from KingOfPoptart/patcheddit (a fork of wchill/patcheddit), GPLv3.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.morphe.patches.syncextras.redgifs

import app.morphe.patcher.Fingerprint

// ImageViewerFragment$g0.onErrorResponse(VolleyError): the single failure funnel for
// Sync's Redgifs playback flow. The token request, the /info request, and the actual
// /v2/gifs/<id> request all deliver errors to this listener, which then shows
// "Error connecting to Redgifs". Its `a` field holds the original redgifs.com URL.
internal object RedgifsErrorListenerFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/ImageViewerFragment\$g0;",
    name = "onErrorResponse",
    parameters = listOf("Lcom/android/volley/VolleyError;"),
    returnType = "V",
)

// WebViewFragment$a.onPageFinished(WebView, String): the in-app browser's
// page-finished callback. Used to auto-accept the CookieYes banner on redgifs pages.
internal object WebViewClientOnPageFinishedFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/WebViewFragment\$a;",
    name = "onPageFinished",
    parameters = listOf("Landroid/webkit/WebView;", "Ljava/lang/String;"),
    returnType = "V",
)

// WebViewFragment.o2(View, Bundle) = onViewCreated: wipes ALL cookies via
// CookieManager.removeAllCookie() before loadUrl. Patched to skip the wipe for
// redgifs URLs so the consent cookie persists between opens.
internal object WebViewFragmentOnViewCreatedFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/WebViewFragment;",
    name = "o2",
    parameters = listOf("Landroid/view/View;", "Landroid/os/Bundle;"),
    returnType = "V",
)
