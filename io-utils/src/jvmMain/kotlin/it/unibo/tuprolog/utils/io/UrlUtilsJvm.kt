package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.utils.io.exceptions.InvalidUrlException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(protocol = "file", path = path)

actual fun remoteUrl(protocol: String, host: String, port: Int?, path: String, query: String?): Url =
    JvmUrl(protocol, host, port, path, query)

fun URL.toUrl(): Url = parseUrl(toExternalForm())

fun Url.toURL(): URL = when (this) {
    is JvmUrl -> url
    else -> toString().toUrl()
}

internal fun String.toUrl(): URL =
    try {
        URI(this).takeIf { it.isAbsolute }
            ?.toURL()
            ?: throw InvalidUrlException(message = "Invalid URL: $this")
    } catch (e: MalformedURLException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    } catch (e: URISyntaxException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    }

actual fun Url.toFile(): File = JvmFile(toURL().file)

actual fun File.toUrl(): Url = Url.file(path)
