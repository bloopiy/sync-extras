package app.morphe.patches.syncextras

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch

/** Same target as wchill/patcheddit, so both bundles apply to the same stock APK. */
internal val SyncForReddit = Compatibility(
    name = "Sync for Reddit",
    packageName = "com.laurencedawson.reddit_sync",
    targets = listOf(AppTarget(version = "v23.06.30-13:39"))
)

internal const val EXTENSION_PACKAGE = "Lapp/morphe/extension/syncextras"

/**
 * Merges this bundle's own extension (app.morphe.extension.syncextras.*).
 *
 * This deliberately does NOT use wchill's ExtensionPatches.Sync. wchill's extension
 * patch merges shared.mpe + syncforreddit.mpe + its HTTP library; pulling those in
 * from a second bundle is what produced duplicate-class failures with the old fork.
 */
internal val syncExtrasExtensionPatch = bytecodePatch(default = false) {
    extendWith("extensions/syncextras.mpe")
}
