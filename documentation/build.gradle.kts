import java.io.OutputStream
import java.io.PrintStream

plugins {
    id("org.jetbrains.dokka")
}

val plantUml = configurations.create("plantUml") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>(Usage.JAVA_RUNTIME))
        attribute(
            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
            objects.named<TargetJvmEnvironment>(TargetJvmEnvironment.STANDARD_JVM),
        )
    }
}

dependencies {
    rootProject.subprojects
        .filter { it.path != project.path }
        .forEach { add("dokka", project(it.path)) }
    plantUml(libs.plantuml)
}

val diagramsDir = file("diagrams")
val generatedDiagramsDir = file("docs/assets/diagrams")
val plantUmlFiles = fileTree(diagramsDir) { include("**/*.puml") }

val generateDiagrams = tasks.register<JavaExec>("generateDiagrams") {
    description = "Generate diagrams from PlantUML files"
    group = "MkDocs"
    inputs.files(plantUmlFiles)
    outputs.dir(generatedDiagramsDir)
    classpath = plantUml
    mainClass.set("net.sourceforge.plantuml.Run")
    doFirst { generatedDiagramsDir.mkdirs() }
    // ponytail: smetana is PlantUML's pure-Java layout engine, used to avoid a native Graphviz
    // dependency in CI/dev machines; switch to the (higher-fidelity) `dot`-based layout by installing
    // Graphviz and dropping this flag if diagram layout quality ever becomes an issue.
    args("-tsvg", "-Playout=smetana", "-o", generatedDiagramsDir.absolutePath)
    args(plantUmlFiles.map { it.absolutePath })
}

val mkdocsSiteDir = layout.buildDirectory.dir("site")

val checkMkdocsCommandExists = tasks.register<Exec>("checkMkdocsCommandExists") {
    description = "Check if the mkdocs command is available"
    group = "MkDocs"
    commandLine("mkdocs", "--version")
}

fun Exec.configureMkdocs(vararg commands: String) {
    group = "MkDocs"
    dependsOn("generateDiagrams")
    dependsOn("checkMkdocsCommandExists")
    inputs.dir("docs")
    inputs.file("mkdocs.yml")
    outputs.dir(mkdocsSiteDir)
    workingDir = projectDir
    standardOutput = System.out
    errorOutput = System.out
    commandLine(*commands)
}

val mkdocsBuild = tasks.register<Exec>("mkdocsBuild") {
    description = "Build the MkDocs site"
    configureMkdocs("mkdocs", "build", "--site-dir", mkdocsSiteDir.get().asFile.absolutePath)
}

val assembleSite = tasks.register<Copy>("assembleSite") {
    description = "Build the Assemble site"
    group = "MkDocs"
    dependsOn(mkdocsBuild, "dokkaGenerateHtml")
    from(mkdocsSiteDir)
    from(layout.buildDirectory.dir("dokka/html")) { into("api") }
    into(layout.buildDirectory.dir("assembledSite"))
}

tasks.named("assemble") {
    dependsOn(assembleSite)
}

val serveMkdocs = tasks.register<Exec>("serveMkdocs") {
    description = "Serve the MkDocs site locally"
    configureMkdocs("mkdocs", "serve")
}
