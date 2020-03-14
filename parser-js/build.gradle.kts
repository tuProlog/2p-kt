import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import java.io.File

plugins {
    kotlin("js")
    id("maven-publish")
    signing
    id("org.jetbrains.dokka")
    id("com.jfrog.bintray")
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
val ktFreeCompilerArgs: String by project

val generatedSrcDir = "$buildDir/generated-src/antlr/main"

dependencies {
    antlr("org.antlr", "antlr4", Versions.org_antlr)
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
            api(npm("antlr4", "^${Versions.org_antlr.replace("-1", ".0")}"))
        }
    }

    with(sourceSets["test"]) {
        with(kotlin) {
            srcDir("src/test/js")
            srcDir("src/test/kotlin")
        }
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
                logger.debug(
                    "java -cp {} {} {}",
                    classpath.joinToString(File.pathSeparator),
                    main,
                    args?.joinToString(" ") ?: ""
                )
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

val thisProject = project

listOf(thisProject, rootProject.subprojects.first { it.name == "parser-core" }).forEach {
    with(it) {
        tasks.withType<KotlinJsCompile> {
            dependsOn(generateGrammarSource)
            val compilationDir = File(kotlinOptions.outputFile!!).parentFile

            val copyJsFilesTask = tasks.create<Copy>(name.replace("Kotlin", "")) {
                from("${thisProject.projectDir}/src/main/js")
                if ("test" in name.toLowerCase()) {
                    from("${thisProject.projectDir}/src/test/js")
                }
                from("$generatedSrcDir/js")
                include("**/*")
                into(compilationDir)
            }

            this.finalizedBy(copyJsFilesTask)
        }
    }
}

tasks.create("jsTest") {
    dependsOn("test")
}

tasks.getByName<Jar>("sourcesJar") {
    kotlin.sourceSets.forEach { sourceSet ->
        sourceSet.resources.sourceDirectories.forEach {
            from(it)
        }
        sourceSet.kotlin.srcDirs.forEach {
            from(it)
        }
    }
}