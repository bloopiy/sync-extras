group = "app.morphe"

patches {
    about {
        name = "Sync Extras"
        description = "Add-on patches for Sync for Reddit. Use alongside wchill/patcheddit, not instead of it."
        source = "git@github.com:bloopiy/sync-extras.git"
        author = "bloopiy"
        contact = "https://github.com/bloopiy/sync-extras/issues"
        website = "https://github.com/bloopiy/sync-extras"
        license = "GPLv3. Contains code from Patcheddit; additional GPL section 7 terms apply, see NOTICE."
    }
}

dependencies {
    // Used by the library's JSON generator.
    implementation(libs.gson)

    // Required due to smali, or build fails. Can be removed once smali is bumped.
    implementation(libs.guava)

    implementation(libs.morphe.patches.library)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.morphe.util.PatchListGeneratorKt")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}
