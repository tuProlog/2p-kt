package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import it.unibo.tuprolog.solve.channel.WriterChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import java.io.File
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

internal actual val platformFileSystem: FileSystem = FileSystem.SYSTEM

actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(File(path).toURI().toURL())

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JvmUrl(protocol, host, port, path, query)

/** Wraps a plain `java.net.` [URL] into a [Url], for interop with code that already has one (e.g. JVM APIs). */
fun URL.toUrl(): Url = parseUrl(toExternalForm())

/**
 * Unwraps this [Url] into a plain `java.net.` [URL], for interop with JVM APIs that require one.
 *
 * Returns the wrapped [JvmUrl.url] directly when this is already a [JvmUrl] (the common case, since [parseUrl]
 * always produces one); otherwise round-trips through [toString] and [String.toUrl].
 */
fun Url.toURL(): URL =
    when (this) {
        is JvmUrl -> url
        else -> toString().toUrl()
    }

/**
 * Parses this string into a `java.net.` [URL], backing [JvmUrl]'s string constructor.
 *
 * Goes through [URI] rather than [URL]'s own (deprecated) string constructor, since [URI] validates syntax more
 * strictly; [URI.isAbsolute] is additionally required so that relative/scheme-less strings (e.g. bare filesystem
 * paths) are rejected here, letting [Url.Companion.of]'s `file://`-prefix fallback handle them instead.
 *
 * @throws InvalidUrlException if this string is not a well-formed, absolute URL.
 */
internal fun String.toUrl(): URL =
    try {
        URI(this)
            .takeIf { it.isAbsolute }
            ?.toURL()
            ?: throw InvalidUrlException(message = "Invalid URL: $this")
    } catch (e: MalformedURLException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    } catch (e: URISyntaxException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    }

internal actual fun Url.toLocalPath(): Path = File(toURL().toURI()).toOkioPath()

/**
 * Streams a local file lazily off disk via [LocalFileSystem]; for any non-[Url.isFile] URL (`http(s)`, ...),
 * lazily streams from `java.net.`[URL.openStream], unlike the JS implementation which eagerly buffers remote
 * resources in full.
 */
actual fun Url.openInputChannel(): InputChannel<String> =
    if (isFile) {
        ReaderChannel(LocalFileSystem.source(toLocalPath()).buffer().inputStream())
    } else {
        ReaderChannel(toURL().openStream())
    }

/**
 * Only supported for [Url.isFile] URLs: writing to a remote resource is never supported (on any platform).
 * @throws IOException unless [Url.isFile].
 */
actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val path = toLocalPath()
    val sink = if (append) LocalFileSystem.appendingSink(path) else LocalFileSystem.sink(path)
    return WriterChannel(sink.buffer().outputStream())
}
