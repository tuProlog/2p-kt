@file:JvmName("UrlUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import okio.Path
import kotlin.jvm.JvmName

/** Platform-specific factory backing [Url.Companion.file]: builds a `file://`-scheme [Url] for [path]. */
expect fun fileUrl(path: String): Url

/** Platform-specific factory backing [Url.Companion.remote]. */
expect fun remoteUrl(
    protocol: String,
    host: String = "",
    port: Int? = null,
    path: String = "",
    query: String? = null,
): Url

/**
 * Platform-specific factory backing [Url.Companion.of]: parses [string] into a [Url].
 * @throws it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException if [string] is not a well-formed URL.
 */
expect fun parseUrl(string: String): Url

/**
 * Opens this [Url] for reading, e.g. to back `open(Url, read, Stream)` (see
 * [it.unibo.tuprolog.solve.libs.io.primitives.Open3]) or [it.unibo.tuprolog.solve.libs.io.primitives.Consult].
 *
 * On the JVM this works for any [Url] whose [Url.protocol] `java.net.URL` can open a stream for (files, `http(s)`,
 * ...); on JS it eagerly [Url.readAsText]s the resource into an in-memory [InputChannel].
 *
 * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot be opened for reading.
 */
expect fun Url.openInputChannel(): InputChannel<String>

/**
 * Opens this [Url] for writing (or, if [append], for appending), e.g. to back `open(Url, write, Stream)` (see
 * [it.unibo.tuprolog.solve.libs.io.primitives.Open3]).
 *
 * Writing is only supported for [Url.isFile] URLs on the JVM, and is never supported on JS.
 *
 * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if this [Url] is not a writable file (on the JVM),
 * or unconditionally on JS.
 */
expect fun Url.openOutputChannel(append: Boolean = false): OutputChannel<String>

/**
 * Resolves this (local, i.e. [Url.isFile]) [Url] to an [okio.Path] usable with [LocalFileSystem].
 * Behavior is unspecified for non-local URLs.
 */
internal expect fun Url.toLocalPath(): Path
