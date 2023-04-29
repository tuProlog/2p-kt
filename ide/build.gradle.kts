plugins {
    `kotlin-jvm-only`
    application
    alias(libs.plugins.javafx)
    id("com.github.johnrengelman.shadow")
    `kotlin-doc`
    `publish-on-maven`
}

val arguments: String? by project

val supportedPlatforms by extra { listOf("win", "linux", "mac", "mac-aarch64") }

dependencies {
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(project(":parser-theory"))
    api(project(":solve-classic"))
    api(libs.richtextFx)

    libs.javafx.graphics.get().let {
        val dependencyNotation = "${it.module.group}:${it.module.name}:${it.versionConstraint.preferredVersion}"
        supportedPlatforms.forEach { platform ->
            runtimeOnly("$dependencyNotation:$platform")
        }
    }

    testImplementation(kotlin("test-junit"))
}

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

val entryPoint = "it.unibo.tuprolog.ui.gui.Main"

application {
    mainClass.set(entryPoint)
}

shadowJar(entryPoint)

for (platform in supportedPlatforms) {
    shadowJar(entryPoint, platform)
}
