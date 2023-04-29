

plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    id("com.github.johnrengelman.shadow")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.clikt)
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

val supportedPlatforms by extra { emptyList<String>() }

shadowJar(entryPoint = mainKlass)

tasks.create("run", JavaExec::class.java) {
    group = "application"
    dependsOn("jvmMainClasses")
    classpath = files(
        kotlin.jvm().compilations.getByName("main").output,
        kotlin.jvm().compilations.getByName("main").compileDependencyFiles
    )
    standardInput = System.`in`
    mainClass.set(mainKlass)
    arguments.let {
        if (it != null) {
            args = it.split("\\s+".toRegex()).filterNot { a -> a.isBlank() }
        }
    }
}
