package app.morphe.patches.syncextras

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.Patch
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.util.logging.Logger

/*
 * Integration with wchill/patcheddit's Morphe settings menu, WITHOUT depending on it.
 *
 * wchill's menu is data-driven: its patches write entries into res/xml/morphe_settings.xml
 * during `execute`, and its MorpheSettingsActivity renders whatever that file contains,
 * storing switch values in the app's default SharedPreferences. We rely only on that
 * runtime contract, never on wchill's classes:
 *
 *   - file      res/xml/morphe_settings.xml, root <PreferenceScreen>
 *   - sections  child <PreferenceScreen android:key="morphe_category_<slug>">; unknown
 *               keys are shown after wchill's own sections
 *   - storage   default SharedPreferences (<package>_preferences)
 *   - keys      "morphe_patch_" prefix gets wchill's "restart the app" toast
 *
 * Our entries are added in `finalize`, which runs after every patch's `execute`, so the
 * file exists whatever order the two bundles' patches run in. If wchill's menu isn't in
 * the APK (bundle not selected, or the contract changed) we skip quietly and every
 * Sync Extras patch keeps its default behaviour. Nothing here can fail a patch run.
 */

private const val WCHILL_MENU_FILE = "res/xml/morphe_settings.xml"
internal const val SETTINGS_CLASS = "$EXTENSION_PACKAGE/SyncExtrasSettings;"

private val logger = Logger.getLogger("SyncExtrasSettings")

/** wchill's section titles (PatchCategory). Same title = same section in the menu. */
internal object SettingsSection {
    const val MEDIA = "Media"
    const val UNDELETE = "Undelete"
    const val OTHER = "Other patches"
}

internal class SyncExtrasSetting(
    /** SharedPreferences key; must match SyncExtrasSettings.java for toggles. */
    val key: String,
    val title: String,
    val summary: String,
    val section: String,
    /** true = informational entry ("included in this APK"), no switch. */
    val readOnly: Boolean = false,
    val defaultEnabled: Boolean = true,
) {
    init {
        require(key.matches(Regex("morphe_patch_syncextras_[a-z0-9_]+"))) { "Invalid Sync Extras key: $key" }
    }
}

internal object RedditApplicationOnCreateFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/RedditApplication;",
    name = "onCreate",
    parameters = listOf(),
    returnType = "V",
)

/** Gives the extension a Context so it can read the switches. */
internal val syncExtrasSettingsContextPatch = bytecodePatch(default = false) {
    dependsOn(syncExtrasExtensionPatch)

    execute {
        RedditApplicationOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static { p0 }, $SETTINGS_CLASS->initialize(Landroid/content/Context;)V"
        )
    }
}

/**
 * A hidden resource patch that adds [setting] to wchill's menu if it's present.
 * Each user-facing patch depends on the one for its own setting.
 */
internal fun syncExtrasSettingPatch(setting: SyncExtrasSetting): Patch<*> = resourcePatch(default = false) {
    finalize {
        try {
            val menu = this[WCHILL_MENU_FILE, false]
            if (!menu.exists()) {
                logger.info("wchill's Morphe settings menu isn't in this APK; '${setting.title}' stays at its default.")
                return@finalize
            }
            document(WCHILL_MENU_FILE).use { addSyncExtrasEntry(it, setting) }
        } catch (error: Exception) {
            logger.warning("Couldn't add '${setting.title}' to wchill's settings menu: ${error.message}")
        }
    }
}

/** Pure DOM edit, kept separate so it can be tested without a patcher. */
internal fun addSyncExtrasEntry(menu: Document, setting: SyncExtrasSetting): Boolean {
    val root = menu.documentElement
    if (root.tagName.substringAfterLast('.') != "PreferenceScreen") {
        logger.warning("Unexpected root <${root.tagName}> in wchill's settings menu; skipping '${setting.title}'.")
        return false
    }

    val all = root.getElementsByTagName("*")
    val existingKeys = (0 until all.length).map { (all.item(it) as Element).getAttribute("android:key") }.toSet()
    if (setting.key in existingKeys) return false // already added, e.g. by a re-run

    val sectionKey = "morphe_category_" + setting.section.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    val section = (0 until root.childNodes.length)
        .map { root.childNodes.item(it) }
        .filterIsInstance<Element>()
        .firstOrNull { it.getAttribute("android:key") == sectionKey }
        ?: menu.createElement("PreferenceScreen").apply {
            setAttribute("android:key", sectionKey)
            setAttribute("android:title", setting.section)
            setAttribute("android:persistent", "false")
            root.appendChild(this)
        }

    val note = if (setting.readOnly) {
        "From Sync Extras. Included in this APK; repatch the app to change it."
    } else {
        "From Sync Extras. Restart the app after changing this switch."
    }
    section.appendChild(
        menu.createElement(if (setting.readOnly) "Preference" else "CheckBoxPreference").apply {
            setAttribute("android:key", setting.key)
            setAttribute("android:title", setting.title)
            setAttribute("android:summary", listOf(setting.summary, note).filter(String::isNotBlank).joinToString("\n"))
            if (setting.readOnly) {
                setAttribute("android:selectable", "false")
                setAttribute("android:persistent", "false")
            } else {
                setAttribute("android:defaultValue", setting.defaultEnabled.toString())
            }
        }
    )
    return true
}
