import java.io.OutputStream
import java.io.PrintStream

plugins {
    id("org.jetbrains.dokka")
}

// Needed to reference :ide-web's zipWebDistribution task below: without it, whether that task has been
// registered yet depends on unrelated project-evaluation ordering.
evaluationDependsOn(":ide-web")

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
        .filter { it.path != project.path && it.path != ":ide-web" }
        // :ide-web is a browser application, not a library: everything in it is internal/private, so there's
        // no public API for Dokka to document anyway. (The actual, since-fixed bug that made including it here
        // corrupt its own separate production webpack build lived in the root build.gradle.kts - see the
        // otherProjects commit - not in this loop; excluded regardless, since Dokka has nothing to say about it.)
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
    configureMkdocs("mkdocs", "build", "--strict", "--site-dir", mkdocsSiteDir.get().asFile.absolutePath)
}

// The web IDE is a fully static, self-contained SPA (see :ide-web's zipWebDistribution), so publishing it is
// just unpacking that zip into a subpath of the docs site; no separate hosting/build step is needed.
val webIdeDistribution = project(":ide-web").tasks.named("zipWebDistribution")

val assembleSite = tasks.register<Copy>("assembleSite") {
    description = "Build the Assemble site"
    group = "MkDocs"
    dependsOn(mkdocsBuild, "dokkaGenerateHtml", webIdeDistribution)
    from(mkdocsSiteDir)
    from(layout.buildDirectory.dir("dokka/html")) { into("api") }
    from(zipTree(webIdeDistribution.map { it.outputs.files.singleFile })) { into("web-ide") }
    into(layout.buildDirectory.dir("assembledSite"))
}

//tasks.named("assemble") {
//    dependsOn(assembleSite)
//}

val serveMkdocs = tasks.register<Exec>("serveMkdocs") {
    description = "Serve the MkDocs site locally"
    configureMkdocs("mkdocs", "serve")
    outputs.upToDateWhen { false }
}
