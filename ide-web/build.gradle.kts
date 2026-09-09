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
            implementation(libs.kotlinx.coroutines.core)
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
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

tasks.named("jsProcessResources") {
    dependsOn(copyIdeLogo)
    (this as Copy).from(layout.buildDirectory.dir("generated-resources/logo"))
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
