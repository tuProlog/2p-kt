import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.gciatto.kt.mpp.ProjectConfiguration.configureUploadToGithub

plugins {
    id("com.github.johnrengelman.shadow")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.github.ajalt:clikt-multiplatform:_")
                api(project(":core"))
                api(project(":oop-lib"))
                api(project(":io-lib"))
                api(project(":solve-classic"))
                api(project(":parser-theory"))
            }
        }
    }
}

val arguments: String? by project
val mainKlass = "it.unibo.tuprolog.ui.repl.Main"

val shadowJar by tasks.getting(ShadowJar::class) {
    dependsOn("jvmMainClasses")
    archiveBaseName.set("${rootProject.name}-${project.name}")
    archiveClassifier.set("redist")
    configurations = listOf(
        kotlin.jvm().compilations.getByName("main").compileDependencyFiles as Configuration
    )
    from(kotlin.jvm().compilations.getByName("main").output)
    from(files("${rootProject.projectDir}/LICENSE"))
    manifest {
        attributes("Main-Class" to mainKlass)
    }
}

configureUploadToGithub(shadowJar)

tasks.create("run", JavaExec::class.java) {
    group = "application"
    dependsOn("jvmMainClasses")
    classpath = files(
        kotlin.jvm().compilations.getByName("main").output,
        kotlin.jvm().compilations.getByName("main").compileDependencyFiles
    )
    standardInput = System.`in`
    main = mainKlass
    arguments.let {
        if (it != null) {
            args = it.split("\\s+".toRegex()).filterNot { a -> a.isBlank() }
        }
    }
}
