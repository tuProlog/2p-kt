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
        classpath = runtimeClasspath
    }
    standardInput = System.`in`
    project.findProperty("arguments")?.let {
        args = it.toString().split("\\s+".toRegex()).filterNot(String::isBlank)
    }
}
