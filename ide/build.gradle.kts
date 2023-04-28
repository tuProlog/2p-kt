import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.configurationcache.extensions.capitalized

plugins {
    `kotlin-jvm-only`
    application
    alias(libs.plugins.javafx)
    alias(libs.plugins.shadowJar)
    `kotlin-doc`
    `publish-on-maven`
}

val arguments: String? by project

val supportedPlatforms = listOf("win", "linux", "mac", "mac-aarch64")

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

fun shadowJar(
    platform: String? = null,
    name: String = "shadowJar" + (platform?.let { "For${it.capitalized()}" } ?: ""),
    excludedPlatforms: List<String> = platform?.let { supportedPlatforms - it } ?: emptyList()
): ShadowJar = tasks.maybeCreate(name, ShadowJar::class.java).also { jarTask ->
    jarTask.manifest { attributes("Main-Class" to entryPoint) }
    jarTask.archiveBaseName.set("${rootProject.name}-${project.name}")
    jarTask.archiveVersion.set(project.version.toString())
    if (platform !== null) {
        jarTask.archiveClassifier.set("redist-$platform")
    } else {
        jarTask.archiveClassifier.set("redist")
    }
    sourceSets.main {
        println("Dependencies for platform ${platform ?: "all"}")
        runtimeClasspath.filter { it.exists() }
            .filter { file -> excludedPlatforms.none { file.name.endsWith("$it.jar") }.also {
                if (!it) println("Exclude $file")
            } }
            .map { if (it.isDirectory) it else zipTree(it) }
            .forEach { println(it) ; jarTask.from(it) }
    }
    jarTask.from(files("${rootProject.projectDir}/LICENSE"))
    jarTask.dependsOn("classes")
    tasks.maybeCreate("allShadowJars").also {
        it.dependsOn(jarTask)
        it.group = "shadow"
    }
}

val shadowJar = shadowJar()

for (platform in supportedPlatforms) {
    shadowJar(platform)
}
