plugins {
    id(libs.plugins.shadowJar.get().pluginId)
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

val arguments: String? by project

val supportedPlatforms by extra { listOf("win", "linux", "mac", "mac-aarch64") }

dependencies {
    api(project(":ide"))
    api(project(":solve-problog"))
    api(libs.graphviz)
    testImplementation(kotlin("test-junit"))
}

val entryPoint = "it.unibo.tuprolog.ui.gui.PLPMain"

tasks.create<JavaExec>("run") {
    group = "application"
    mainClass.set(entryPoint)
    dependsOn("jvmMainClasses")
    classpath = sourceSets.getByName("main").runtimeClasspath
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
