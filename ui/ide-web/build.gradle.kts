plugins {
    id("org.jetbrains.kotlin.multiplatform")
    alias(libs.plugins.kotlin.serialization)
    id("io.github.gciatto.kt-mpp.linter")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":gui"))
            implementation(project(":core"))
            implementation(project(":gui-prolog"))
            implementation(project(":solve-classic"))
            implementation(project(":io-lib"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(npm("ace-builds", "1.44.0"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":parser-theory"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>().configureEach {
    mainOutputFileName.set("ide-web.js")
}

wireDefaultLogoResource("jsProcessResources")
wireIdeIconsResource("jsProcessResources")

// Ace's own module system predates ES/CommonJS modules and expects to be loaded as a global script rather
// than bundled by webpack (see Ace.kt); vendor its two prebuilt files as plain assets loaded from index.html
// instead of importing them through Kotlin/JS, so the packaged distribution stays fully self-contained/offline.
val copyAceEditor =
    tasks.register<Copy>("copyAceEditor") {
        dependsOn(rootProject.tasks.named("kotlinNpmInstall"))
        from(rootProject.layout.buildDirectory.dir("js/node_modules/ace-builds/src-min-noconflict")) {
            include("ace.js", "theme-github.js", "theme-github_dark.js")
        }
        into(layout.buildDirectory.dir("generated-resources/ace"))
    }

tasks.named("jsProcessResources") {
    dependsOn(copyAceEditor)
    (this as Copy).from(layout.buildDirectory.dir("generated-resources/ace"))
}

val webDistribution = tasks.named("jsBrowserDistribution")

val zipWebDistribution =
    tasks.register<Zip>("zipWebDistribution") {
        group = "distribution"
        description = "Packs the ide-web browser distribution into a ready-to-unpack zip archive."
        dependsOn(webDistribution)
        from(webDistribution.map { it.outputs.files.singleFile })
        archiveBaseName.set("ide-web")
        destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    }

tasks.named("assemble") {
    dependsOn(zipWebDistribution)
}

// jsBrowserTest only ever mounts individual Kotlin classes in isolation; it never boots index.html, so it
// cannot catch bugs in how the app is wired together end to end (see scripts/browser-e2e-test.mjs for examples
// this has actually caught). Not part of `check`/`build`: it needs a local Chrome/Chromium and Node on PATH
// (set $CHROME_BIN to point at a non-default Chrome install) and is slower than the unit tests.
tasks.register<Exec>("browserE2ETest") {
    group = "verification"
    description = "Runs scripts/browser-e2e-test.mjs against a real headless Chrome loading the packaged " +
        "ide-web distribution."
    dependsOn(webDistribution)
    executable = "node"
    doFirst {
        args(
            layout.projectDirectory
                .file("scripts/browser-e2e-test.mjs")
                .asFile.absolutePath,
            webDistribution
                .get()
                .outputs.files.singleFile.absolutePath,
        )
    }
}

// browserE2ETest is deliberately NOT wired into `check`/`jsTest` (via dependsOn OR finalizedBy). Both were tried
// and both reproduced a real, consistent failure at the time: running jsBrowserTest and browserE2ETest's
// production webpack build within the same Gradle invocation produced a production bundle with its main()
// silently reduced to nothing (root cause since found and fixed: a root build.gradle.kts bug duplicated this
// module's Kotlin JS target configuration - see the `otherProjects` comment there). Left decoupled here since
// that was never re-verified after the real fix landed, and a separate CI step works regardless. CI therefore
// runs `./gradlew :ide-web:browserE2ETest` as its own step, after the main check step, not as a dependency of it.
