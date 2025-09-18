import io.github.gciatto.kt.mpp.jar.javaFxFatJars

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
    classpath = sourceSets.getByName("main").runtimeClasspath
    standardInput = System.`in`
    project.findProperty("arguments")?.let {
        args = it.toString().split("\\s+".toRegex()).filterNot(String::isBlank)
    }
}
