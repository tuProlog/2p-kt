apply(plugin = "org.jetbrains.dokka")

val plantUml: Configuration by configurations.creating {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
        attribute(
            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
            objects.named(TargetJvmEnvironment::class, TargetJvmEnvironment.STANDARD_JVM),
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

val generateDiagrams by tasks.registering(JavaExec::class) {
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

val mkdocsBuild by tasks.registering(Exec::class) {
    dependsOn(generateDiagrams)
    inputs.dir("docs")
    inputs.file("mkdocs.yml")
    outputs.dir(mkdocsSiteDir)
    workingDir = projectDir
    commandLine("mkdocs", "build", "--site-dir", mkdocsSiteDir.get().asFile.absolutePath)
}

val assembleSite by tasks.registering(Copy::class) {
    dependsOn(mkdocsBuild, "dokkaGenerateHtml")
    from(mkdocsSiteDir)
    from(layout.buildDirectory.dir("dokka/html")) { into("api") }
    into(layout.buildDirectory.dir("assembledSite"))
}

tasks.named("assemble") {
    dependsOn(assembleSite)
}
