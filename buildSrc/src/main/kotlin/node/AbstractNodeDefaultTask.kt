package node

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property

abstract class AbstractNodeDefaultTask : DefaultTask() {

    @Input
    protected val jsCompileTask: Property<String> = project.objects.property()

    init {
        group = "nodeJs"
        dependsOn(jsCompileTask)
    }

    open fun defaultValuesFrom(extension: NpmPublishExtension) {
        jsCompileTask.set(extension.jsCompileTask)
    }
}