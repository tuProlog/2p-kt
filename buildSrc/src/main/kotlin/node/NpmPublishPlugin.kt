package node

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec

class NpmPublishPlugin : Plugin<Project> {

    private lateinit var extension: NpmPublishExtension

    private fun Project.createNpmLoginTask(name: String): DefaultTask {
        val setRegistryName = "${name}SetRegistry"
        val setRegistry = tasks.maybeCreate(setRegistryName, Exec::class.java).also {
            it.group = "nodeJs"
            it.standardOutput = System.out
        }
        val setToken = tasks.maybeCreate("${name}SetToken", Exec::class.java).also {
            it.dependsOn(setRegistry)
            it.group = "nodeJs"
            it.standardOutput = System.out
        }
        extension.onExtensionChanged.add {
            setRegistry.executable = node.absolutePath
            setRegistry.setArgs(listOf(npm, "set", "registry", "https://$registry/"))
            setToken.executable = node.absolutePath
            setToken.setArgs(listOf(npm, "set", "//$registry/:_authToken", token))
            nodeSetupTask?.let {
                setRegistry.dependsOn(it)
                setToken.dependsOn(it)
            }
        }
        return tasks.maybeCreate(name, DefaultTask::class.java).also {
            it.group = "nodeJs"
            it.dependsOn(setToken)
        }
    }

    private fun Project.createNpmPublishTask(name: String): Exec {
        val publish = tasks.maybeCreate(name, Exec::class.java).also {
            it.group = "nodeJs"
            it.standardOutput = System.out
        }
        extension.onExtensionChanged.add {
            publish.executable = node.absolutePath
            publish.setArgs(listOf(npm, "publish", npmProject, "--access", "public"))
            nodeSetupTask?.let { publish.dependsOn(it) }
            jsCompileTask?.let { publish.dependsOn(it) }
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
            jsCompileTask?.let { copy.dependsOn(it) }
        }
        return copy
    }

    private fun Project.createLiftPackageJsonTask(name: String): LiftPackageJsonTask {
        val lift = tasks.maybeCreate(name, LiftPackageJsonTask::class.java).also {
            it.group = "nodeJs"
        }
        extension.onExtensionChanged.add {
            lift.packageJsonFile = packageJson
            lift.liftingActions = liftingActions
            lift.rawLiftingActions = rawLiftingActions
            jsCompileTask?.let { lift.dependsOn(it) }
        }
        return lift
    }

    override fun apply(target: Project) {
        extension = target.extensions.create("greeting", NpmPublishExtension::class.java)
        val login = target.createNpmLoginTask("npmLogin")
        val publish = target.createNpmPublishTask("npmPublish")
        val lift = target.createLiftPackageJsonTask("liftPackageJson")
        val copy = target.createCopyRootProjectFilesTask("copyFilesNextToPackageJson")
        publish.dependsOn(login)
        publish.dependsOn(lift)
        lift.dependsOn(copy)
    }
}

