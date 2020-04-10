package node

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

open class LiftJsSourcesTask : DefaultTask() {

    internal var liftingActions: List<FileLineTransformer> = emptyList()
        @Internal
        get() = field
        set(value) {
            field = value
        }

    @Internal
    lateinit var jsSourcesDir: File

    @TaskAction
    fun lift() {
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
                        for (liftAction in liftingActions) {
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