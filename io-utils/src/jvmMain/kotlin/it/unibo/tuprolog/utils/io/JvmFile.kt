package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.utils.io.exceptions.IOException
import java.io.File as JavaFile
import java.io.IOException as JavaIOException

@Suppress("MemberVisibilityCanBePrivate")
data class JvmFile(val file: JavaFile) : File {
    constructor(path: String) : this(JavaFile(path))

    override val path: String
        get() = file.absolutePath

    override fun readText(): String =
        try {
            file.readText()
        } catch (e: JavaIOException) {
            throw IOException(e.message, e)
        }

    override val name: String
        get() = file.name

    override val parent: File
        get() = JvmFile(file.parentFile)

    override fun rename(name: String): JvmFile = JvmFile(file.parentFile.resolve(name))

    fun setPath(path: String): JvmFile = JvmFile(path)

    override fun writeText(text: String) {
        try {
            file.writeText(text)
        } catch (e: JavaIOException) {
            throw IOException(e.message, e)
        }
    }

    override fun toString(): String = file.toString()
}
