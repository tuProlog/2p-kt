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
}

dependencies {
    api(project(":ide"))
    api(project(":solve-problog"))
    api(libs.graphviz)
    testImplementation(kotlin("test-junit"))
}

tasks.register<JavaExec>("run") {
    group = "application"
    mainClass.set(multiPlatformHelper.fatJarEntryPoint)
    dependsOn(tasks.named("jvmMainClasses"))
    classpath = sourceSets.getByName("main").runtimeClasspath.withoutForeignJavaFxPlatforms()
    standardInput = System.`in`
    project.findProperty("arguments")?.let {
        args = it.toString().split("\\s+".toRegex()).filterNot(String::isBlank)
    }
}
