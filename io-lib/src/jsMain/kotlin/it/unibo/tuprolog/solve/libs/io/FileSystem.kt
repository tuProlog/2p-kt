package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import kotlinx.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.fetch.Request
import kotlin.js.Promise

private val FS by lazy {
    js("require('fs')")
}

private val READ_TEXT = js("{ encoding: 'UTF-8', flag: 'r' }")

private val READ_BINARY = js("{ encoding: 'binary', flag: 'r' }")

private val FETCH by lazy {
    if (Info.PLATFORM == Platform.BROWSER) {
        window
    } else {
        js("{ fetch: require('node-fetch') }")
    }
}

private fun fetch(url: String): Promise<Request> {
    return FETCH.fetch(url).unsafeCast<Promise<Request>>()
}

private fun toByteArray(obj: dynamic): ByteArray =
    ByteArray(obj.length) { obj[it] }

private fun Uint8Array.toByteArray(): ByteArray =
    ByteArray(length) { this[it] }

private fun ArrayBuffer.toUInt8Array(): Uint8Array =
    Uint8Array(this)

private fun ArrayBuffer.toByteArray(): ByteArray =
    toUInt8Array().toByteArray()

@Suppress("UNUSED_PARAMETER")
private fun browserReadText(path: String): String =
    throw IOException("Reading a local file in browser is not supported, yet")

private fun nodeReadText(path: String): String =
    try {
        FS.readFileSync(path, READ_TEXT).unsafeCast<String>()
    } catch (e: Throwable) {
        throw IOException("Error while reading file $path", e)
    }

fun readText(path: String): String =
    if (Info.PLATFORM == Platform.BROWSER) {
        browserReadText(path)
    } else {
        nodeReadText(path)
    }

@Suppress("UNUSED_PARAMETER")
private fun browserReadBin(path: String): ByteArray =
    throw IOException("Reading a local file in browser is not supported, yet")

private fun nodeReadBin(path: String): ByteArray =
    try {
        toByteArray(FS.readFileSync(path, READ_BINARY))
    } catch (e: Throwable) {
        throw IOException("Error while reading file $path", e)
    }

fun readBin(path: String): ByteArray =
    if (Info.PLATFORM == Platform.BROWSER) {
        browserReadBin(path)
    } else {
        nodeReadBin(path)
    }

@Suppress("UNUSED_PARAMETER")
private fun browserReadTextAsync(path: String, callback: (String?, IOException?) -> Unit) =
    callback(null, IOException("Reading a local file in browser is not supported, yet"))

private fun nodeReadTextAsync(path: String, callback: (String?, IOException?) -> Unit) =
    FS.readFile(path, READ_TEXT) { err: Throwable?, data: String? ->
        if (err != null) {
            callback(null, IOException("Error while reading file $path", err))
        } else {
            callback(data!!, null)
        }
    }

fun readTextAsync(path: String, callback: (String?, IOException?) -> Unit) =
    if (Info.PLATFORM == Platform.BROWSER) {
        browserReadTextAsync(path, callback)
    } else {
        nodeReadTextAsync(path, callback)
    }

@Suppress("UNUSED_PARAMETER")
private fun browserReadBinAsync(path: String, callback: (ByteArray?, IOException?) -> Unit) =
    callback(null, IOException("Reading a local file in browser is not supported, yet"))

private fun nodeReadBinAsync(path: String, callback: (ByteArray?, IOException?) -> Unit) =
    FS.readFile(path, READ_BINARY) { err: Throwable?, data: dynamic ->
        if (err != null) {
            callback(null, IOException("Error while reading file $path", err))
        } else {
            callback(toByteArray(data!!), null)
        }
    }

fun readBinAsync(path: String, callback: (ByteArray?, IOException?) -> Unit) =
    if (Info.PLATFORM == Platform.BROWSER) {
        browserReadBinAsync(path, callback)
    } else {
        nodeReadBinAsync(path, callback)
    }

fun fetchTextAsync(url: String, callback: (String?, IOException?) -> Unit) {
    fetch(url).then { req ->
        req.text()
            .then { callback(it, null) }
            .catch { callback(null, IOException("Error while reading URL $url", it)) }
    }.catch {
        callback(null, IOException("Error while reading URL $url", it))
    }
}

fun fetchBinAsync(url: String, callback: (ByteArray?, IOException?) -> Unit) {
    fetch(url).then { req ->
        req.arrayBuffer()
            .then { callback(it.toByteArray(), null) }
            .catch { callback(null, IOException("Error while reading URL $url", it)) }
    }.catch {
        callback(null, IOException("Error while reading URL $url", it))
    }
}
