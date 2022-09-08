package it.unibo.tuprolog.utils.io

interface File {
    val path: String

    val name: String

    val parent: File

    fun rename(name: String): File

    fun readText(): String

    fun writeText(text: String)

    companion object {
        fun of(path: String): File = fileOf(path)
    }
}
