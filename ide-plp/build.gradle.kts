import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-jvm-only`
    application
    alias(libs.plugins.javafx)
    alias(libs.plugins.shadowJar)
    `kotlin-doc`
    `publish-on-maven`
}

val arguments: String? by project

dependencies {
    api(project(":ide"))
    api(project(":solve-problog"))
    api(libs.graphviz)

    libs.javafx.graphics.get().let {
        val dependencyNotation = "${it.module.group}:${it.module.name}:${it.versionConstraint.preferredVersion}"
        listOf("win", "linux", "mac").forEach { platform ->
            runtimeOnly("$dependencyNotation:$platform")
        }
    }

    testImplementation(kotlin("test-junit"))
}

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

val entryPoint = "it.unibo.tuprolog.ui.gui.PLPMain"

application {
    mainClass.set(entryPoint)
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
    manifest { attributes("Main-Class" to entryPoint) }
    archiveBaseName.set("${rootProject.name}-${project.name}")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    sourceSets.main {
        runtimeClasspath.filter { it.exists() }
            .map { if (it.isDirectory) it else zipTree(it) }
            .forEach { from(it) }
    }
    from(files("${rootProject.projectDir}/LICENSE"))
    dependsOn("classes")
}
