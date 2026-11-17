# Sync Extras

Add-on Morphe patches for **Sync for Reddit** (`v23.06.30-13:39`). It contains only the
patches that [wchill/patcheddit](https://github.com/wchill/patcheddit) doesn't have.
**Add it next to wchill's bundle in Morphe Manager, not instead of it.**

| Patch | Default | Source |
|---|---|---|
| Unlock Sync Ultra | on | humzakh/HK-Morphe-Patches via KingOfPoptart |
| Fix "Restore Comment" (needs Ultra; uses Arctic Shift) | on | humzakh/HK-Morphe-Patches via KingOfPoptart |
| Open Redgifs links in WebView on failure | on | KingOfPoptart (idea: pdscomp) |

**Use wchill v2.3.0 or newer** (stable). It carries the working Redgifs fix (`/info`
emulation, token refresh, malformed-link fix). Older wchill releases lack it, so every Redgifs
video fails and drops to the WebView. Keep wchill's **Fix Redgifs API** turned on, and enabled
in Sync's Morphe settings. The WebView patch here is only a fallback.

## Why this doesn't clash with wchill

* **Own extension namespace.** Code lives in `app.morphe.extension.syncextras`, loaded from
  `extensions/syncextras.mpe`. It never pulls in wchill's `shared.mpe` /
  `syncforreddit.mpe`, so no class is merged twice.
* **Unique patch names.** No patch here has the same name as one in wchill's bundle.
* **Different methods.** None of these patches touches a method that wchill's
  Sync patches modify.
* **Same toolchain** as wchill (Morphe plugin 2.0.0, patches library 2.1.0).

## Use it

1. Push this repo to GitHub and replace `bloopiy` in `patches/build.gradle.kts`.
2. The **Build and release** Action builds the `.mpp`, publishes release `v<version>` and
   updates `patches-bundle.json`. Bump `version` in `gradle.properties` for each new release.
3. In Morphe Manager, **remove the old KingOfPoptart bundle**. Then add this repo's bundle:
   `https://raw.githubusercontent.com/bloopiy/sync-extras/main/patches-bundle.json`
   (or the release `.mpp`).
4. Use wchill's bundle: `https://raw.githubusercontent.com/wchill/patcheddit/main/patches-bundle.json`.
   Patch a **stock** Sync APK with both bundles.

Build locally: `./gradlew :patches:buildAndroid` (needs the Android SDK, plus a GitHub
token with `read:packages` in `gpr.user`/`gpr.key` for the Morphe plugin).

## License

GPLv3. The Redgifs patches derive from Patcheddit code, so Patcheddit's GPLv3 §7 terms in
`NOTICE` apply. That's why they show the required attribution toast once after each
install or update of the patched app, and why this project isn't named after Patcheddit.
