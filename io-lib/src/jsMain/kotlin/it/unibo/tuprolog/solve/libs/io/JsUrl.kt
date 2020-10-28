package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.HOST
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PATH
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PORT
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PROTOCOL
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.QUERY
import it.unibo.tuprolog.solve.libs.io.Url.Companion.ensureValidPort
import it.unibo.tuprolog.solve.libs.io.Url.Companion.parse
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException

class JsUrl(url: String) : Url {

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this(Url.toString(protocol, host, port, path, query))

    override val protocol: String

    override val host: String

    override val path: String

    override val port: Int?

    override val query: String?

    init {
        val match = parse(url) ?: throw InvalidUrlException("Invalid URL: $url")
        protocol = match[PROTOCOL] ?: ""
        host = match[HOST] ?: ""
        path = match[PATH] ?: ""
        port = match[PORT]?.toInt()?.ensureValidPort()
        query = match[QUERY]
    }

    override fun readAsText(): String {
        if (isFile) {
            return readText(path)
        } else {
            throw IOException("Reading a remote file synchronously is not supported, yet")
        }
    }

    override fun readAsByteArray(): ByteArray {
        if (isFile) {
            return readBin(path)
        } else {
            throw IOException("Reading a remote file synchronously is not supported, yet")
        }
    }

    override fun readAsTextAsync(callback: (String?, IOException?) -> Unit) {
        if (isFile) {
            readTextAsync(path, callback)
        } else {
            fetchTextAsync(toString(), callback)
        }
    }

    override fun readAsByteArrayAsync(callback: (ByteArray?, IOException?) -> Unit) {
        if (isFile) {
            readBinAsync(path, callback)
        } else {
            fetchBinAsync(toString(), callback)
        }
    }

    override fun toString(): String {
        return Url.toString(protocol, host, port, path, query)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as JsUrl

        if (protocol != other.protocol) return false
        if (host != other.host) return false
        if (path != other.path) return false
        if (port != other.port) return false
        if (query != other.query) return false

        return true
    }

    override fun hashCode(): Int {
        var result = protocol.hashCode()
        result = 31 * result + host.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + (port ?: 0)
        result = 31 * result + (query?.hashCode() ?: 0)
        return result
    }
}