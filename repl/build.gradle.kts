import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.shadowJar)
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
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

val shadowJar by tasks.getting(ShadowJar::class) {
    dependsOn("jvmMainClasses")
    archiveBaseName.set("${rootProject.name}-${project.name}")
    archiveClassifier.set("redist")
    from(kotlin.jvm().compilations.getByName("main").output)
    from(files("${rootProject.projectDir}/LICENSE"))
    manifest {
        attributes("Main-Class" to mainKlass)
    }
}

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
