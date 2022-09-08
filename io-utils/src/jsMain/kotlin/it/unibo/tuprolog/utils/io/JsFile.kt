package it.unibo.tuprolog.utils.io

data class JsFile(val url: Url) : File {

    init {
        require(url.isFile)
    }

    constructor(path: String) : this(
        parseUrl(if (path.startsWith("file://")) path else "file://$path")
    )

    override val path: String
        get() = url.path

    override val name: String by lazy { path.split("/").last() }

    override val parent: File
        get() = JsFile(path.replace("/$name", ""))

    override fun rename(name: String): File = JsFile(path.replace(this.name, name))

    override fun readText(): String = url.readAsText()

    override fun writeText(text: String) = throw NotImplementedError("Writing files on JS is currently not supported")

    override fun toString(): String = path
}
