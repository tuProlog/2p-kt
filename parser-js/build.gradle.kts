import node.Bugs
import node.NpmPublishExtension
import node.NpmPublishPlugin
import node.People
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask
import java.io.File

apply<NpmPublishPlugin>()

val antlr by configurations.creating {
    setTransitive(true)
}

val javaVersion: String by project
val ktFreeCompilerArgs: String by project
val gcName: String by project
val gcEmail: String by project
val gcUrl: String by project
val projectHomepage: String by project
val bintrayRepo: String by project
val bintrayUserOrg: String by project
val projectLicense: String by project
val projectIssues: String by project
val npmToken: String by project

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
            api(kotlin("stdlib-js"))
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
                "-listener",
                "-visitor",
                "-package", "${rootProject.group}.parsing",
                antlrFile.absolutePath
            )
        }
    }
}

configure<NpmPublishExtension> {
    nodeRoot = rootProject.tasks.withType<NodeJsSetupTask>().asSequence().map { it.destination }.first()
    token = npmToken
    packageJson = tasks.getByName<KotlinPackageJsonTask>("packageJson").packageJson
    nodeSetupTask = rootProject.tasks.getByName("kotlinNodeJsSetup").path
    jsCompileTask = "mainClasses"

    liftPackageJson {
        people = mutableListOf(People(gcName, gcEmail, gcUrl))
        homepage = projectHomepage
        bugs = Bugs(projectIssues, "gcEmail")
        license = projectLicense
        name = "@tuprolog/$name"
        dependencies = dependencies?.mapKeys {
            if ("2p" in it.key) "@tuprolog/${it.key}" else it.key
        }?.toMutableMap()
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
