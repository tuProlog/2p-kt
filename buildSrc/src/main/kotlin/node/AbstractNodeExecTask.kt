package node

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import java.io.File

abstract class AbstractNodeExecTask(requiresCompilation: Boolean = false) : Exec() {

    @Input
    protected val nodeSetupTask: Property<String> = project.objects.property()

    @Input
    protected val jsCompileTask: Property<String> = project.objects.property()

    @Input
    protected val node: Property<File> = project.objects.property()

    @Input
    protected val npm: Property<File> = project.objects.property()

    @Input
    protected val registry: Property<String> = project.objects.property()

    init {
        group = "nodeJs"
        standardOutput = System.out
        errorOutput = System.err
        dependsOn(nodeSetupTask)
        if (requiresCompilation) {
            dependsOn(jsCompileTask)
        }
    }

    open fun defaultValuesFrom(extension: NpmPublishExtension) {
        nodeSetupTask.set(extension.nodeSetupTask)
        jsCompileTask.set(extension.jsCompileTask)
        node.set(extension.node)
        npm.set(extension.npm)
        registry.set(extension.registry)
    }

    @TaskAction
    override fun exec() {
        executable = node.get().absolutePath
        args(*setupArguments())
        afterSetup()
        configSecurityWarning()
        super.exec()
    }

    protected abstract fun setupArguments(): Array<String>

    protected open fun afterSetup() {
        println("Executing: ${commandLine.joinToString()}")
    }

    private fun configSecurityWarning() {
        listOf(executable, args?.get(0)).forEach {
            if (it == null || !File(it).exists()) {
                System.err.println("[WARNING] [$path] Missing executable $it")
            }
        }
    }
}