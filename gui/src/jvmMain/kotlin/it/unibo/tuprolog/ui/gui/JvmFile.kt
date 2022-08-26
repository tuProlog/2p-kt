package it.unibo.tuprolog.ui.gui

import java.io.File as JavaFile

@Suppress("MemberVisibilityCanBePrivate")
class JvmFile(val file: JavaFile) : File {
    constructor(path: String) : this(JavaFile(path))

    override val path: String
        get() = file.absolutePath

    override fun readText(): String = file.readText()

    override val name: String
        get() = file.name

    override fun rename(name: String): JvmFile = JvmFile(file.parentFile.resolve(name))

    fun setPath(path: String): JvmFile = JvmFile(path)

    override fun writeText(text: String) {
        file.writeText(text)
    }
}
