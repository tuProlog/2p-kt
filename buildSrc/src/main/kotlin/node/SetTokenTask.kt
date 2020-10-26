package node

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property

open class SetTokenTask : AbstractNodeExecTask() {
    override fun setupArguments(): Array<String> {
        return arrayOf(
            npm.get().absolutePath,
            "set",
            "//${registry.get()}/:_authToken",
            token.get()
        )
    }

    @Input
    protected val token: Property<String> = project.objects.property()

    override fun afterSetup() {
        println("Executing: ${commandLine.subList(0, commandLine.lastIndex).joinToString()} <TOKEN>")
    }

    override fun defaultValuesFrom(extension: NpmPublishExtension) {
        super.defaultValuesFrom(extension)
        token.set(extension.token)
    }
}