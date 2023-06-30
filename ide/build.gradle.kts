plugins {
    id(libs.plugins.shadowJar.get().pluginId)
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

val arguments: String? by project

val supportedPlatforms by extra { listOf("win", "linux", "mac", "mac-aarch64") }

dependencies {
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(project(":parser-theory"))
    api(project(":solve-classic"))
    api(libs.richtextFx)
    api(libs.javafx.controls)
    api(libs.javafx.fxml)
    libs.javafx.graphics.get().let {
        val dependencyNotation = "${it.module.group}:${it.module.name}:${it.versionConstraint.preferredVersion}"
        supportedPlatforms.forEach { platform ->
            runtimeOnly("$dependencyNotation:$platform")
        }
    }

    testImplementation(kotlin("test-junit"))
}

val entryPoint = "it.unibo.tuprolog.ui.gui.Main"

tasks.create<JavaExec>("run") {
    group = "application"
    mainClass.set(entryPoint)
    dependsOn("jvmMainClasses")
    sourceSets.getByName("main") {
        classpath = runtimeClasspath
    }
    standardInput = System.`in`
}

shadowJar(entryPoint)

for (platform in supportedPlatforms) {
    if ("mac" in platform) {
        shadowJar(entryPoint, platform, excludedPlatforms = supportedPlatforms - setOf(platform, "linux"))
    } else {
        shadowJar(entryPoint, platform)
    }
}
