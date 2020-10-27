package node

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

open class LiftJsSourcesTask : AbstractNodeDefaultTask() {

    @Input
    val liftingActions = project.objects.listProperty<FileLineTransformer>()

    @Input
    val jsSourcesDir: Property<File> = project.objects.property()

    override fun defaultValuesFrom(extension: NpmPublishExtension) {
        super.defaultValuesFrom(extension)
        jsSourcesDir.set(extension.jsSourcesDir)
        liftingActions.set(extension.jsSourcesLiftingActions)
    }

    @TaskAction
    fun lift() {
        val jsSourcesDir = this.jsSourcesDir.get()
        if (!jsSourcesDir.exists() || !jsSourcesDir.isDirectory) {
            throw IllegalStateException("$jsSourcesDir is not a valid directory path")
        }
        for (file in project.fileTree(jsSourcesDir) { include("**/*.js") }) {
            val temp = temporaryDir.resolve("${file.nameWithoutExtension}-${file.hashCode()}.js")
            temp.createNewFile()
            BufferedWriter(OutputStreamWriter(temp.outputStream())).use { output ->
                file.useLines {
                    for (indexedLine in it.withIndex()) {
                        var line = indexedLine.value
                        for (liftAction in liftingActions.getOrElse(emptyList())) {
                            line = liftAction.invoke(file, indexedLine.index, line)
                        }
                        output.write(line)
                        output.newLine()
                    }
                }
            }
            file.delete()
            temp.renameTo(file)
        }
    }
}