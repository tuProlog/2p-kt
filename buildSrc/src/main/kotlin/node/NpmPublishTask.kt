package node

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property
import java.io.File

open class NpmPublishTask : AbstractNodeExecTask(requiresCompilation = true) {
    override fun setupArguments(): Array<String> {
        return arrayOf(
            npm.get().absolutePath,
            "publish",
            npmProject.get().absolutePath,
            "--access",
            "public"
        )
    }

    @Input
    protected val npmProject: Property<File> = project.objects.property()

    override fun defaultValuesFrom(extension: NpmPublishExtension) {
        super.defaultValuesFrom(extension)
        npmProject.set(extension.npmProject)
    }
}