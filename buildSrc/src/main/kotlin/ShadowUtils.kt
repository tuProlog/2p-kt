import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

fun String.toPascalCase(separators: Set<Char> = setOf('_', '-')) =
    split(*separators.toCharArray()).joinToString("") {
        it.capitalized()
    }

private val Project.supportedPlatforms: List<String>
    get() {
        val supportedPlatforms: List<String>? by extra
        return supportedPlatforms ?: emptyList()
    }

fun Project.shadowJar(
    entryPoint: String? = null,
    platform: String? = null,
    name: String = "shadowJar" + (platform?.let { "For${it.toPascalCase()}" } ?: ""),
    classifier: String = "redist",
    excludedPlatforms: List<String> = platform?.let { supportedPlatforms - it } ?: emptyList()
): ShadowJar {
    fun setOfFileSystemLocationsToFileTree(locations: Set<FileSystemLocation>): FileTree =
        locations.map { it.asFile }.map { if (it.isDirectory) fileTree(it) else zipTree(it) }.reduce(FileTree::plus)
    fun fileShouldBeIncluded(file: File): Boolean = excludedPlatforms.none { file.name.endsWith("$it.jar") }
    return tasks.maybeCreate(name, ShadowJar::class.java).also { jarTask ->
        entryPoint?.let { jarTask.manifest { attributes("Main-Class" to it) } }
        jarTask.archiveBaseName.set(project.provider { "${rootProject.name}-${project.name}" })
        jarTask.archiveVersion.set(project.provider { project.version.toString() })
        if (platform !== null) {
            jarTask.archiveClassifier.set("$classifier-$platform")
        } else {
            jarTask.archiveClassifier.set(classifier)
        }
        configureJarForProject(jarTask, ::fileShouldBeIncluded, ::setOfFileSystemLocationsToFileTree)
        jarTask.from(files("${rootProject.projectDir}/LICENSE"))
        tasks.maybeCreate("allShadowJars").also {
            it.dependsOn(jarTask)
            it.group = "shadow"
        }
    }
}

private fun Project.configureJarFromFileCollection(
    jarTask: ShadowJar,
    fileCollection: FileCollection,
    shouldBeIncluded: (File) -> Boolean,
    toFileTree: (Set<FileSystemLocation>) -> FileTree,
) = fileCollection.filter { it.exists() }.filter(shouldBeIncluded).elements.map(toFileTree).let { jarTask.from(it) }

private fun Project.configureJarForJvmProject(
    jarTask: ShadowJar,
    shouldBeIncluded: (File) -> Boolean,
    toFileTree: (Set<FileSystemLocation>) -> FileTree
) {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        project.extensions.configure<SourceSetContainer>("sourceSets") {
            getByName("main") {
                configureJarFromFileCollection(jarTask, runtimeClasspath, shouldBeIncluded, toFileTree)
            }
        }
        jarTask.dependsOn("classes")
    }
}

private fun Project.configureJarForMpProject(
    jarTask: ShadowJar,
    shouldBeIncluded: (File) -> Boolean,
    toFileTree: (Set<FileSystemLocation>) -> FileTree
) {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvm().compilations.getByName("main").run {
                for (collection in listOf(output.allOutputs, runtimeDependencyFiles)) {
                    configureJarFromFileCollection(jarTask, collection, shouldBeIncluded, toFileTree)
                }
            }
        }
        jarTask.dependsOn("jvmMainClasses")
    }
}

private fun Project.configureJarForProject(
    jarTask: ShadowJar,
    shouldBeIncluded: (File) -> Boolean,
    toFileTree: (Set<FileSystemLocation>) -> FileTree
) {
    configureJarForMpProject(jarTask, shouldBeIncluded, toFileTree)
    configureJarForJvmProject(jarTask, shouldBeIncluded, toFileTree)
}
