plugins {
    alias(libs.plugins.orchid)
}

configurations {
    val orchidRuntimeOnly by getting {
        resolutionStrategy {
            force(libs.plantuml)
        }
    }
    create("plantuml") {
        isTransitive = true
    }
}

dependencies {
    orchidRuntimeOnly(libs.orchid.docs)
    orchidRuntimeOnly(libs.orchid.kotlinDocs)
    orchidRuntimeOnly(libs.orchid.pluginDocs)

    val plantuml by configurations.getting

    plantuml(libs.plantuml)
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
}

// env ORG_GRADLE_PROJECT_orchidBaseUrl
val orchidBaseUrl: String? by project

orchid {
    theme = "Editorial"
    baseUrl = orchidBaseUrl
    version = rootProject.version.toString()
    args = listOf("--experimentalSourceDoc")
}

fun File.changeExtension(ext: String): File {
    return File(parentFile, "$nameWithoutExtension.$ext")
}

val plantUmlFiles = fileTree("$projectDir/src/orchid/resources/assets/diagrams")
    .also { it.include("**/*.puml").include("**/*.uml") }

if (!plantUmlFiles.isEmpty) {
    val generateUmlDiagramsInSvg by tasks.creating(JavaExec::class) {
        inputs.files(plantUmlFiles)
        outputs.files(
            plantUmlFiles
                .map { it.changeExtension("svg").absolutePath }
                .map { it.replace("diagrams", "generated") }
                .map(::File)
        )
        classpath = configurations.getByName("plantuml")
        mainClass.set("net.sourceforge.plantuml.Run")
        args("-tsvg", "-o", "$projectDir/src/orchid/resources/assets/generated")
        args(plantUmlFiles.map { it.absolutePath })
    }
    tasks.getByName("orchidClasses").dependsOn(generateUmlDiagramsInSvg)
}
