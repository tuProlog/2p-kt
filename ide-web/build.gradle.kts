plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.github.gciatto.kt-mpp.linter")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":gui"))
            implementation(project(":gui-solve"))
            implementation(project(":solve-classic"))
            implementation(project(":io-lib"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(npm("ace-builds", "1.44.0"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":parser-theory"))
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>().configureEach {
    mainOutputFileName.set("ide-web.js")
}

val copyIdeLogo =
    tasks.register<Copy>("copyIdeLogo") {
        from(rootProject.layout.projectDirectory.file(".img/logo.png"))
        into(layout.buildDirectory.dir("generated-resources/logo"))
    }

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
    dependsOn(copyIdeLogo, copyAceEditor)
    (this as Copy).from(
        layout.buildDirectory.dir("generated-resources/logo"),
        layout.buildDirectory.dir("generated-resources/ace"),
    )
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
val browserE2ETest =
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
