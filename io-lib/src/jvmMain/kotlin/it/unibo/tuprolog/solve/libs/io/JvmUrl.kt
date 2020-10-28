package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.net.MalformedURLException
import java.net.URL
import kotlin.streams.asSequence

class JvmUrl(private val url: URL) : Url {

    constructor(string: String) : this(string.toUrl())

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this("$protocol://$host${port?.ensureValidPort()?.str { ":$it" }}$path${query.str { "?$it" }}")

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
        BufferedReader(InputStreamReader(url.openStream())).lines().asSequence().joinToString("\n")

    override fun readAsByteArray(): ByteArray =
        BufferedInputStream(url.openStream()).readAllBytes()

    override fun resolve(child: String): Url = JvmUrl(protocol, host, port, "$path/$child", query)

    companion object {
        private fun <T : Any> T?.str(transformation: (T) -> String = { it.toString() }): String =
            if (this == null) "" else transformation(this)

        private fun Int.ensureValidPort(): Int =
            if (this < 0) throw IllegalArgumentException("Invalid port: $this") else this

        private fun String.toUrl(): URL =
            try {
                URL(this)
            } catch (e: MalformedURLException) {
                throw InvalidUrlException(
                    message = "Invalid URL: $this",
                    cause = e
                )
            }
    }
}
