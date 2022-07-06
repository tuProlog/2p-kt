import io.github.gciatto.kt.node.LiftJsSourcesTask
import io.github.gciatto.kt.node.LiftPackageJsonTask
import io.github.gciatto.kt.node.NpmPublishExtension
import io.github.gciatto.kt.node.NpmPublishPlugin
import io.github.gciatto.kt.node.NpmPublishTask
import io.github.gciatto.kt.node.PackageJson
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.jvmVersion(provider: Provider<String>) {
    val version = provider.map { JavaVersion.toVersion(it) }.getOrElse(JavaVersion.current())
    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            targetCompatibility = version
            sourceCompatibility = version
        }
    }
    plugins.withType<KotlinPlatformJvmPlugin> {
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = version.toString()
            }
        }
    }
}

val Project.npmCompliantVersion: String
    get() = version.toString().split("+")[0]

fun Project.nodeVersion(default: Provider<String>, override: Any? = null) {
    plugins.withType<NodeJsRootPlugin> {
        configure<NodeJsRootExtension> {
            val requestedVersion = override?.toString() ?: default.takeIf { it.isPresent }?.get() ?: nodeVersion
            nodeVersion = NodeVersions.toFullVersion(requestedVersion)
            project.logger.log(LogLevel.LIFECYCLE, "Using NodeJS v{} for project {}", nodeVersion, project.path)
        }
    }
}

fun Project.packageJson(handler: PackageJson.() -> Unit) {
    plugins.withType<NpmPublishPlugin> {
        configure<NpmPublishExtension> {
            liftPackageJson(handler)
        }
    }
}

fun Project.liftPackageJsonToFixDependencies(packageJson: PackageJson) {
    packageJson.dependencies = packageJson.dependencies?.filterKeys { key -> "kotlin-test" !in key }
        ?.mapValues { (key, value) ->
            val temp = if (value.startsWith("file:")) {
                value.split('/', '\\').last()
            } else {
                value
            }
            if (rootProject.name in key) temp.substringBefore('+') else temp
        }?.toMutableMap()
}

fun Project.liftPackageJsonToSetOrganization(organizationName: String, packageJson: PackageJson) {
    packageJson.name = "@$organizationName/${packageJson.name}"
    packageJson.dependencies = packageJson.dependencies
        ?.mapKeys { (key, _) ->
            if (rootProject.name in key) "@$organizationName/$key" else key
        }?.toMutableMap()
}

fun Project.liftJsSourcesToSetOrganization(organizationName: String, line: String): String =
    line.replace("'${rootProject.name}", "'@$organizationName/${rootProject.name}")
        .replace("\"${rootProject.name}", "\"@$organizationName/${rootProject.name}")

fun Project.subprojects(names: Iterable<String>): Set<Project> {
    return subprojects.filter { it.name in names }.toSet()
}

fun Project.subprojects(first: String, vararg others: String): Set<Project> {
    val names = setOf(first, *others)
    return subprojects(names)
}

fun Project.subprojects(names: Iterable<String>, except: Iterable<String> = emptySet(), action: Project.() -> Unit) {
    rootProject.subprojects {
        if (name.let { it in names && it !in except }) {
            action()
        }
    }
}

fun Project.subprojects(names: Iterable<String>, except: String, action: Project.() -> Unit) {
    subprojects(names, setOf(except), action)
}

fun Project.subprojects(first: String, vararg others: String, action: Project.() -> Unit) {
    val names = setOf(first, *others)
    subprojects(names, emptySet(), action)
}

fun Iterable<Project>.except(names: Iterable<String>): Set<Project> {
    return filter { it.name !in names }.toSet()
}

fun Iterable<Project>.except(first: String, vararg others: String): Set<Project> {
    val names = setOf(first, *others)
    return except(names)
}

val Provider<MinimalExternalModuleDependency>.version
    get() = this.get().versionConstraint.requiredVersion

fun Project.npmSubproject(subproject: String, organizationName: String? = null): Pair<String, String> =
    rootProject.project(":$subproject").run {
        val org = organizationName ?: project.findProperty("npmOrganization")?.toString()
        (org?.let { "@$it/" } ?: "") + "${rootProject.name}-$subproject" to npmCompliantVersion
    }
