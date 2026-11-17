/*
 * Ported from humzakh/HK-Morphe-Patches (https://github.com/humzakh/HK-Morphe-Patches), GPLv3,
 * via KingOfPoptart/patcheddit.
 */
package app.morphe.patches.syncextras.ultra

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.syncextras.SETTINGS_CLASS
import app.morphe.patches.syncextras.SettingsSection
import app.morphe.patches.syncextras.SyncExtrasSetting
import app.morphe.patches.syncextras.SyncForReddit
import app.morphe.patches.syncextras.syncExtrasExtensionPatch
import app.morphe.patches.syncextras.syncExtrasSettingPatch
import app.morphe.patches.syncextras.syncExtrasSettingsContextPatch

// Key must match SyncExtrasSettings.UNLOCK_SYNC_ULTRA.
internal val ultraSetting = SyncExtrasSetting(
    key = "morphe_patch_syncextras_unlock_sync_ultra",
    title = "Unlock Sync Ultra",
    summary = "Unlocks Sync Ultra Lifetime. Fix \"Restore Comment\" needs this on.",
    section = SettingsSection.OTHER,
)

// Runs the unlock only while the switch is on; otherwise falls through to Sync's own check.
private const val ULTRA_GATE = """
                invoke-static { }, $SETTINGS_CLASS->isUltraUnlocked()Z
                move-result v0
                if-eqz v0, :syncextras_ultra_off
"""

@Suppress("unused")
val syncUltraPatch = bytecodePatch(
    name = "Unlock Sync Ultra",
    description = "Unlocks Sync Ultra Lifetime.",
) {
    compatibleWith(SyncForReddit)

    dependsOn(syncExtrasExtensionPatch, syncExtrasSettingsContextPatch, syncExtrasSettingPatch(ultraSetting))

    execute {
        // Return true when checking for a Sync Ultra subscription.
        UltraHelperFingerprint.method.addInstructions(
            0,
            ULTRA_GATE + """
                const/4 v0, 0x1
                return v0
                :syncextras_ultra_off
            """
        )

        // Return "Sync Ultra Lifetime" when checking the subscription type.
        SyncUltraSubTypeFingerprint.method.addInstructions(
            0,
            ULTRA_GATE + """
                const-string v0, "Sync Ultra Lifetime"
                return-object v0
                :syncextras_ultra_off
            """
        )

        // Return true when checking whether the user is an Ultra lifetime member.
        UltraLifetimeFingerprint.method.addInstructions(
            0,
            ULTRA_GATE + """
                const/4 v0, 0x1
                return v0
                :syncextras_ultra_off
            """
        )
    }
}
