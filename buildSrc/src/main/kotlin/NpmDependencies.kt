import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import java.io.File

val Project.localPackageJsonFile: File?
    get() = runCatching { projectDir.resolve("package.json") }.getOrNull()

val Project.packageJsonFile: File?
    get() = localPackageJsonFile.also {
        if (it == null) {
            logger.warn("package.json file not found in project ${project.name}, looking in root project instead")
        }
    } ?: rootProject.localPackageJsonFile

private val jsonMapper = ObjectMapper()

private fun parsePackageJson(file: File): ObjectNode =
    file.reader().use { reader -> jsonMapper.readTree(reader) as ObjectNode }

class JsDependenciesFrom(private val packageJson: ObjectNode) {
    constructor(packageJsonFile: File?) : this (
        packageJsonFile
            ?.takeIf { it.exists() }
            ?.let { parsePackageJson(it) }
            ?: throw IllegalStateException("package.json file not found in project ${packageJsonFile?.parentFile?.name}")
    )

    private fun ObjectNode.getDependencyVersion(name: String, sort: String? = null): String {
        val key = if (sort == null) "dependencies" else "${sort}Dependencies"
        return get(key)?.get(name)?.asText()
            ?: throw IllegalStateException("Dependency $name not found in package.json")
    }

    fun KotlinDependencyHandler.npm(name: String, optional: Boolean = false): Dependency {
        val version = packageJson.getDependencyVersion(name)
        if (optional) {
            return optionalNpm(name, version)
        } else {
            return npm(name, version)
        }
    }

    fun KotlinDependencyHandler.devNpm(name: String): Dependency =
        devNpm(name, packageJson.getDependencyVersion(name))

    fun KotlinDependencyHandler.peerNpm(name: String): Dependency =
        peerNpm(name, packageJson.getDependencyVersion(name))
}

fun jsDependenciesFrom(
    packageJson: ObjectNode,
    action: JsDependenciesFrom.() -> Unit,
): JsDependenciesFrom = JsDependenciesFrom(packageJson).apply(action)

fun jsDependenciesFrom(
    packageJsonFile: File?,
    action: JsDependenciesFrom.() -> Unit,
): JsDependenciesFrom = JsDependenciesFrom(packageJsonFile).apply(action)
