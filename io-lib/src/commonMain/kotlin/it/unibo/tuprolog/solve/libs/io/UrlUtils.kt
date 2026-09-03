@file:JvmName("UrlUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import okio.Path
import kotlin.jvm.JvmName

expect fun fileUrl(path: String): Url

expect fun remoteUrl(
    protocol: String,
    host: String = "",
    port: Int? = null,
    path: String = "",
    query: String? = null,
): Url

expect fun parseUrl(string: String): Url

expect fun Url.openInputChannel(): InputChannel<String>

expect fun Url.openOutputChannel(append: Boolean = false): OutputChannel<String>

/**
 * Resolves this (local, i.e. [Url.isFile]) [Url] to an [okio.Path] usable with [LocalFileSystem].
 * Behavior is unspecified for non-local URLs.
 */
internal expect fun Url.toLocalPath(): Path
