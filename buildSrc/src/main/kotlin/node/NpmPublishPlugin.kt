package node

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import java.io.File

class NpmPublishPlugin : Plugin<Project> {

    private lateinit var extension: NpmPublishExtension

    private fun Exec.configSecurityWarning() {
        doFirst {
            listOf(executable, args?.get(0)).forEach {
                if (it == null || !File(it).exists()) {
                    System.err.println("[WARNING] [$path] Missing executable $it")
                }
            }
        }
    }

    private fun NpmPublishExtension.setDependenciesFrom(
        task1: Task,
        vararg otherTasks: Task,
        taskNameSelector: NpmPublishExtension.() -> String?
    ) {
        val setupTask = this.taskNameSelector() ?: return
        sequenceOf(task1, *otherTasks).forEach {
            if (it.project.tasks.findByPath(setupTask) != null ||
                it.project.rootProject.tasks.findByPath(setupTask) != null) {
                it.dependsOn(setupTask)
            } else {
                error("Task ${it.path} cannot depend on $setupTask, since the latter does not exist")
            }
        }
    }

    private fun Project.createNpmLoginTask(name: String): DefaultTask {
        val setRegistryName = "${name}SetRegistry"
        val setRegistry = rootProject.tasks.maybeCreate(setRegistryName, Exec::class.java).also {
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.errorOutput = System.err
            it.configSecurityWarning()
        }
        val setToken = rootProject.tasks.maybeCreate("${name}SetToken", Exec::class.java).also {
            it.dependsOn(setRegistry)
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.errorOutput = System.err
            it.configSecurityWarning()
        }
        extension.onExtensionChanged.add {
            setRegistry.executable = node.absolutePath
            setRegistry.setArgs(listOf(npm, "set", "registry", "https://$registry/"))
            setToken.executable = node.absolutePath
            setToken.setArgs(listOf(npm, "set", "//$registry/:_authToken", token))
            setDependenciesFrom(setRegistry, setToken) { nodeSetupTask }
        }
        return rootProject.tasks.maybeCreate(name, DefaultTask::class.java).also {
            it.group = "nodeJs"
            it.dependsOn(setToken)
        }
    }

    private fun Project.createNpmPublishTask(name: String): Exec {
        val publish = tasks.maybeCreate(name, Exec::class.java).also {
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.errorOutput = System.err
            it.configSecurityWarning()
        }
        extension.onExtensionChanged.add {
            publish.executable = node.absolutePath
            publish.setArgs(listOf(npm, "publish", npmProject, "--access", "public"))
            setDependenciesFrom(publish) { nodeSetupTask }
            setDependenciesFrom(publish) { jsCompileTask }
        }
        return publish
    }

    private fun Project.createCopyRootProjectFilesTask(name: String): Task {
        val copy = tasks.maybeCreate(name, Copy::class.java).also {
            it.group = "nodeJs"
            it.from(rootProject.projectDir)
            it.include("README*")
            it.include("CONTRIB*")
            it.include("LICENSE*")
        }
        extension.onExtensionChanged.add {
            copy.destinationDir = packageJson.parentFile
            setDependenciesFrom(copy) { jsCompileTask }
        }
        return copy
    }

    private fun Project.createLiftJsSourceTask(name: String): DefaultTask {
        val lift = tasks.maybeCreate(name, LiftJsSourcesTask::class.java).also {
            it.group = "nodeJs"
        }
        extension.onExtensionChanged.add {
            lift.jsSourcesDir = jsSourcesDir
            lift.liftingActions = jsSourcesLiftingActions
            setDependenciesFrom(lift) { jsCompileTask }
        }
        return lift
    }

    private fun Project.createLiftPackageJsonTask(name: String): LiftPackageJsonTask {
        val lift = tasks.maybeCreate(name, LiftPackageJsonTask::class.java).also {
            it.group = "nodeJs"
        }
        extension.onExtensionChanged.add {
            lift.packageJsonFile = packageJson
            lift.liftingActions = packageJsonLiftingActions
            lift.rawLiftingActions = packageJsonRawLiftingActions
            setDependenciesFrom(lift) { jsCompileTask }
        }
        return lift
    }

    override fun apply(target: Project) {
        extension = target.extensions.create("greeting", NpmPublishExtension::class.java)
        val login = target.createNpmLoginTask("npmLogin")
        val publish = target.createNpmPublishTask("npmPublish")
        val liftPackageJson = target.createLiftPackageJsonTask("liftPackageJson")
        val copy = target.createCopyRootProjectFilesTask("copyFilesNextToPackageJson")
        val liftJsSourcesTask = target.createLiftJsSourceTask("liftJsSources")
        publish.dependsOn(login)
        publish.dependsOn(liftPackageJson)
        publish.dependsOn(liftJsSourcesTask)
        liftPackageJson.dependsOn(copy)
    }
}

