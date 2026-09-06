import io.github.gciatto.kt.mpp.jar.javaFxFatJars
import org.gradle.internal.os.OperatingSystem.current

fun currentJavaFxPlatform(): String {
    val os = current()
    val archSuffix = if (System.getProperty("os.arch") == "aarch64") "-aarch64" else ""
    return when {
        os.isMacOsX -> "mac$archSuffix"
        os.isWindows -> "win$archSuffix"
        else -> "linux$archSuffix"
    }
}

fun FileCollection.withoutForeignJavaFxPlatforms(): FileCollection {
    val current = currentJavaFxPlatform()
    val foreignSuffixes =
        multiPlatformHelper.fatJarPlatforms.filterNot { it == current }.map { "-$it.jar" }
    return filter { file -> foreignSuffixes.none { file.name.endsWith(it) } }
}

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.fatJar
            .get()
            .pluginId,
    )
}

multiPlatformHelper {
    javaFxFatJars()
    fatJarEntryPoint.set("it.unibo.tuprolog.ui.gui.Main")
}

dependencies {
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(project(":parser-theory"))
    api(project(":solve-classic"))
    api(libs.richtextFx)
    for (jfxModule in listOf(libs.javafx.base, libs.javafx.controls, libs.javafx.fxml, libs.javafx.graphics)) {
        for (platform in multiPlatformHelper.fatJarPlatforms) {
            val dependency =
                jfxModule.get().let {
                    "${it.module.group}:${it.module.name}:${it.versionConstraint.requiredVersion}"
                }
            api("$dependency:$platform")
        }
    }
    testImplementation(kotlin("test-junit"))
}

tasks.register<JavaExec>("run") {
    group = "application"
    mainClass.set(multiPlatformHelper.fatJarEntryPoint)
    dependsOn(tasks.named("jvmMainClasses"))
    sourceSets.getByName("main") {
        // the runtime classpath carries JavaFX natives for every fat-jar platform at once;
        // keep only the current one, otherwise JavaFX may load a foreign-architecture native lib and crash
        classpath = runtimeClasspath.withoutForeignJavaFxPlatforms()
    }
    standardInput = System.`in`
    project.findProperty("arguments")?.let {
        args = it.toString().split("\\s+".toRegex()).filterNot(String::isBlank)
    }
}
