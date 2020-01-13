import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import java.io.File

plugins {
    kotlin("js")
//    id("maven-publish")
//    signing
//    id("org.jetbrains.dokka")
//    id("com.jfrog.bintray")
//    `java-library`
}

val antlr by configurations.creating {
    setTransitive(true)
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/dokka")
}

val javaVersion: String by project
val antlrVersion: String by project
val ktFreeCompilerArgs: String by project

val generatedSrcDir = "$buildDir/generated-src/antlr/main"

dependencies {
    antlr("org.antlr", "antlr4", antlrVersion)
}

kotlin {
    target {
        nodejs()
    }

    with(sourceSets["main"]) {
        with(kotlin) {
            srcDir("src/main/js")
            srcDir("src/main/antlr")
        }
        dependencies {
            implementation(kotlin("stdlib-js"))
            api(npm("antlr4", "^$antlrVersion"))
        }
    }

    with(sourceSets["test"]) {
        dependencies {
            implementation(kotlin("test-js"))
        }
    }
}

val generateGrammarSource = tasks.create<DefaultTask>("generateGrammarSource") {
    group = "antlr"
}

with(fileTree("src/main/antlr")) {
    include("**/*.g4")

    val outputDir = "$generatedSrcDir/js"

    with(generateGrammarSource) {
        inputs.dir("src/main/antlr")
        outputs.dir(outputDir)
    }

    files.forEach { antlrFile ->
        val antlrFileName = antlrFile.name.replace(".g4", "")
        tasks.create<JavaExec>("generateGrammarSource$antlrFileName") {
            group = "antlr"
            generateGrammarSource.dependsOn(this)

            inputs.dir("src/main/antlr")
            outputs.dir(outputDir)

            main = "org.antlr.v4.Tool"
            classpath = antlr
            standardOutput = System.out

            doFirst {
                println("java -cp ${classpath.joinToString(File.pathSeparator)} $main ${args?.joinToString(" ") ?: ""}")
            }

            args(
                "-Dlanguage=JavaScript",
                "-o", outputDir,
                "-message-format", "antlr",
                "-long-messages",
                "-no-listener",
                "-visitor",
                "-package", "${rootProject.group}.parsing",
                antlrFile.absolutePath
            )
        }
    }
}

tasks.withType<KotlinJsCompile> {
    dependsOn(generateGrammarSource)
    val compilationDir = File(kotlinOptions.outputFile!!).parentFile

    val copyJsFilesTask = tasks.create<Copy>(name.replace("Kotlin", "")) {
        from("src/main/js")
        from("$generatedSrcDir/js")
        include("**/*")
        into(compilationDir)
    }

    this.finalizedBy(copyJsFilesTask)
}
