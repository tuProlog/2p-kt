package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import okio.buffer
import okio.use
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import kotlin.streams.asSequence

data class JvmUrl(
    val url: URL,
) : Url {
    constructor(string: String) : this(string.toUrl())

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this(Url.toString(protocol, host, port, path, query))

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
            if (isFile) {
                LocalFileSystem.source(toLocalPath()).buffer().use { it.readUtf8() }
            } else {
                BufferedReader(InputStreamReader(url.openStream())).lines().asSequence().joinToString("\n")
            }
        } catch (e: FileNotFoundException) {
            throw IOException("Cannot find resource: $url", e)
        } catch (e: java.io.IOException) {
            throw IOException("Generic I/O error while accessing: $url", e)
        }

    override fun readAsByteArray(): ByteArray =
        try {
            if (isFile) {
                LocalFileSystem.source(toLocalPath()).buffer().use { it.readByteArray() }
            } else {
                BufferedInputStream(url.openStream()).readAllBytes()
            }
        } catch (e: java.io.IOException) {
            throw IOException(e.message, e)
        }

    override fun toString(): String = url.toString()
}
