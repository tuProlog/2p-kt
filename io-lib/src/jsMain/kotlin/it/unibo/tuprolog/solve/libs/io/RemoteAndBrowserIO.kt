@file:Suppress("TooGenericExceptionCaught")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * `true` when running under Node.js, as opposed to a browser. Local file access is Okio-backed
 * ([NodeJsFileSystem][okio.NodeJsFileSystem]) only under Node, since Okio has no synchronous
 * file system implementation for browsers.
 */
internal val isNode: Boolean
    get() = Info.PLATFORM != Platform.BROWSER

// Okio is not an HTTP client: remote resources (e.g. for consult/1) are still fetched with
// sync-request, since the Solver's I/O is synchronous.
private val REQUEST: dynamic by lazy {
    js("require('sync-request')")
}

internal fun <T> fetch(
    url: String,
    encoding: String? = null,
): T {
    try {
        val response = REQUEST("GET", url, js("{}")) // js("{ cache: 'file' }"))
        return response.getBody(encoding).unsafeCast<T>()
    } catch (e: Throwable) {
        throw IOException(e.message, e)
    }
}

private fun Uint8Array.toByteArray(): ByteArray = ByteArray(length) { this[it] }

private fun ArrayBuffer.toUInt8Array(): Uint8Array = Uint8Array(this)

internal fun ArrayBuffer.toByteArray(): ByteArray = toUInt8Array().toByteArray()

// Browsers have no real file system (and no synchronous one for Okio to wrap), so local "files"
// there remain backed by window.localStorage, as before.
internal fun readText(path: String): String =
    js("window").localStorage.getItem(path).unsafeCast<String?>()
        ?: throw IOException("No such entry in window.localStorage: $path")

@Suppress("UNUSED_PARAMETER")
internal fun readBin(path: String): ByteArray =
    js("window")
        .localStorage
        .getItem(path)
        .unsafeCast<ArrayBuffer?>()
        ?.toByteArray()
        ?: throw IOException("No such entry in window.localStorage: $path")
