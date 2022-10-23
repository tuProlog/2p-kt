package it.unibo.tuprolog.utils.io

import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface File {
    @JsName("path")
    val path: String

    @JsName("name")
    val name: String

    @JsName("parent")
    val parent: File

    @JsName("resolve")
    fun resolve(path: String): File = fileOf(this.path + "/" + path)

    @JsName("div")
    operator fun div(path: String): File = resolve(path)

    @JsName("rename")
    fun rename(name: String): File

    @JsName("readText")
    fun readText(): String

    @JsName("writeText")
    fun writeText(text: String)

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(path: String): File = fileOf(path)

        @JsName("temp")
        @JvmStatic
        @JvmOverloads
        fun temp(name: String, extension: String? = null): File =
            if (extension == null) {
                fileOf(TempPathFinder.directory(name))
            } else {
                fileOf(TempPathFinder.file(name, extension))
            }
    }
}
