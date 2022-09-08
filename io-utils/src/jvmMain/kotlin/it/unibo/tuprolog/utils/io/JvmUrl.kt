package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.utils.io.Url.Companion.toString
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.io.exceptions.InvalidUrlException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import kotlin.streams.asSequence

data class JvmUrl(val url: URL) : Url {

    constructor(string: String) : this(string.toUrl())

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this(toString(protocol, host, port, path, query))

    override val protocol: String
        get() = url.protocol

    override val host: String
        get() = url.host

    override val path: String
        get() = url.path

    override val port: Int?
        get() = url.port.let { if (it > 0) it else null }

    override val query: String?
        get() = url.query

    override fun readAsText(): String =
        try {
            BufferedReader(InputStreamReader(url.openStream())).lines().asSequence().joinToString("\n")
        } catch (e: FileNotFoundException) {
            throw IOException("Cannot find resource: $url", e)
        } catch (e: java.io.IOException) {
            throw IOException("Generic I/O error while accessing: $url", e)
        }

    override fun readAsByteArray(): ByteArray =
        try {
            BufferedInputStream(url.openStream()).readAllBytes()
        } catch (e: java.io.IOException) {
            throw IOException(e.message, e)
        }

    override fun toString(): String = url.toString()

    companion object {
        private fun String.toUrl(): URL =
            try {
                URL(this)
            } catch (e: MalformedURLException) {
                throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
            }
    }
}
