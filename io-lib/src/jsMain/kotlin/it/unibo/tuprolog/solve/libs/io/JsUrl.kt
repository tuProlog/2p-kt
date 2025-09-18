package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.HOST
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PATH
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PORT
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PROTOCOL
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.QUERY
import it.unibo.tuprolog.solve.libs.io.Url.Companion.ensureValidPort
import it.unibo.tuprolog.solve.libs.io.Url.Companion.parse
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import org.khronos.webgl.ArrayBuffer

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

    override fun readAsText(): String {
        if (isFile) {
            return readText(path)
        } else {
            return fetch(url, "UTF-8")
        }
    }

    override fun readAsByteArray(): ByteArray {
        if (isFile) {
            return readBin(path)
        } else {
            return fetch<ArrayBuffer>(url).toByteArray()
        }
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
