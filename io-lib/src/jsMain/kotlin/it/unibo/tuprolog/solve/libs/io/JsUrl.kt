package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.HOST
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PATH
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PORT
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PROTOCOL
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.QUERY
import it.unibo.tuprolog.solve.libs.io.Url.Companion.ensureValidPort
import it.unibo.tuprolog.solve.libs.io.Url.Companion.parse
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import okio.buffer
import okio.use
import org.khronos.webgl.ArrayBuffer
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException as TuPrologIOException

class JsUrl : Url {
    constructor(url: String) {
        val match = parse(url) ?: throw InvalidUrlException("Invalid URL: $url")
        protocol = match[PROTOCOL] ?: ""
        host = match[HOST] ?: ""
        path = match[PATH] ?: ""
        port = match[PORT]?.toInt()?.ensureValidPort()
        query = match[QUERY]
        this.url = url
    }

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) {
        this.protocol = protocol
        this.host = host
        this.port = port
        this.path = path
        this.query = query
        this.url = Url.toString(protocol, host, port, path, query)
    }

    private val url: String

    override val protocol: String

    override val host: String

    override val path: String

    override val port: Int?

    override val query: String?

    override fun readAsText(): String =
        when {
            isFile && isNode -> readLocalFile { it.readUtf8() }
            isFile -> readText(path)
            else -> fetch(url, "UTF-8")
        }

    override fun readAsByteArray(): ByteArray =
        when {
            isFile && isNode -> readLocalFile { it.readByteArray() }
            isFile -> readBin(path)
            else -> fetch<ArrayBuffer>(url).toByteArray()
        }

    private fun <T> readLocalFile(action: (okio.BufferedSource) -> T): T =
        try {
            LocalFileSystem.source(toLocalPath()).buffer().use(action)
        } catch (e: okio.IOException) {
            throw TuPrologIOException(e.message, e)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as JsUrl

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int = url.hashCode()

    override fun toString(): String = url
}
