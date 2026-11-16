/*
 * Ported from humzakh/HK-Morphe-Patches (https://github.com/humzakh/HK-Morphe-Patches), GPLv3,
 * via KingOfPoptart/patcheddit.
 */
package app.morphe.patches.syncextras.ultra

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object UltraHelperFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = listOf(),
    strings = listOf("UltraHelper")
)

internal object SyncUltraSubTypeFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    strings = listOf("Sync Ultra Monthly", "Sync Ultra Yearly", "Sync Ultra lifetime")
)

internal object UltraLifetimeFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = listOf(),
    strings = listOf("ultra_lifetime")
)

internal object CommentHolderBindFingerprint : Fingerprint(
    strings = listOf("+", "[Filtered]")
)

internal object CommentHolderOnProfileClickedFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/viewholders/comments/CommentHolder;",
    name = "onProfileClicked",
    parameters = listOf("Landroid/view/View;"),
    returnType = "V"
)

internal object CommentHolderTFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/viewholders/comments/CommentHolder;",
    name = "T",
    returnType = "Z",
    parameters = listOf(),
    accessFlags = listOf(AccessFlags.PRIVATE)
)

internal object CommentSwipeActionProfileFingerprint : Fingerprint(
    definingClass = "Lgb/m;",
    name = "E",
    parameters = listOf("Lxa/d;", "I"),
    returnType = "V",
    accessFlags = listOf(AccessFlags.PRIVATE)
)

internal object CommentSwipeActionDrawFingerprint : Fingerprint(
    definingClass = "Lgb/m;",
    name = "u",
    parameters = listOf(
        "Landroid/graphics/Canvas;",
        "Landroidx/recyclerview/widget/RecyclerView;",
        "Landroidx/recyclerview/widget/RecyclerView\$c0;",
        "F", "F", "I", "Z"
    ),
    returnType = "V",
    accessFlags = listOf(AccessFlags.PUBLIC)
)

internal object RestoreCommentParseNetworkResponseFingerprint : Fingerprint(
    definingClass = "Lr8/h;",
    name = "parseNetworkResponse",
    parameters = listOf("Lcom/android/volley/NetworkResponse;"),
    returnType = "Lcom/android/volley/Response;"
)
