// Extension code for Sync Extras, compiled to extensions/syncextras.mpe.
//
// Every class lives under app.morphe.extension.syncextras, which wchill/patcheddit
// never uses, so merging this extension next to wchill's never duplicates a class.
// Only plain Android APIs are used: no Morphe shared library, no Sync stubs.
dependencies {
    compileOnly(libs.annotation)
}
