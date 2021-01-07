import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.gciatto.kt.mpp.ProjectConfiguration.configureUploadToGithub

plugins {
    application
    id("org.openjfx.javafxplugin")
    id("com.github.johnrengelman.shadow")
}

val javaFxVersion: String by project
val arguments: String? by project

dependencies {
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(project(":parser-theory"))
    api(project(":solve-classic"))
    api("org.fxmisc.richtext:richtextfx:_")

    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac")

    testImplementation(kotlin("test-junit"))
}

javafx {
    version = javaFxVersion
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

val entryPoint = "it.unibo.tuprolog.ui.gui.Main"

application {
    mainClassName = entryPoint
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
    manifest { attributes("Main-Class" to entryPoint) }
    archiveBaseName.set("${rootProject.name}-${project.name}")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    sourceSets.main {
        runtimeClasspath.filter { it.exists() }
            .map { if (it.isDirectory) it else zipTree(it) }
            .forEach {
                from(it)
            }
    }
    from(files("${rootProject.projectDir}/LICENSE"))
    dependsOn("classes")
}

configureUploadToGithub(shadowJar)
