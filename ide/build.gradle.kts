import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project
val githubToken: String? by project
val arguments: String? by project

plugins {
    application
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
    id("com.github.johnrengelman.shadow")
}

val javaFxVersion: String by project

javafx {
    version = javaFxVersion
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

application {
    // mainModule.set("2p-ide")
    mainClassName = "it.unibo.tuprolog.ui.gui.Main"
}

val linuxOnly by configurations.creating { isTransitive = true }

dependencies {
    api(project(":solve-classic"))
    api(project(":parser-theory"))
    api(project(":oop-lib"))
    api(kotlin("stdlib-jdk8"))
    api("org.fxmisc.richtext:richtextfx:_")

    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac")

    testImplementation(kotlin("test-junit"))
}

java {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.$javaVersion"
        freeCompilerArgs = ktFreeCompilerArgsJvm.split(";").toList()
    }
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
    manifest {
        attributes("Main-Class" to application.mainClassName)
    }
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

    doLast {
        println("Generated: ${archiveFile.get()}")
    }
}

if (!githubToken.isNullOrBlank()) {
    rootProject.configure<GithubReleaseExtension> {
        releaseAssets(*(releaseAssets.toList() + shadowJar).toTypedArray())
    }

    rootProject.tasks.withType(GithubReleaseTask::class) {
        dependsOn(shadowJar)
    }
}
