package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException

// import org.khronos.webgl.ArrayBuffer
// import org.khronos.webgl.Uint8Array
// import org.khronos.webgl.get

private val FS by lazy {
    js("require('fs')")
}

private val READ_TEXT = js("{ encoding: 'UTF-8', flag: 'r' }")

// private val READ_BINARY = js("{ encoding: 'binary', flag: 'r' }")

private val REQUEST: dynamic by lazy {
    js("require('sync-request')")
}

internal fun fetch(url: String, encoding: String): String {
    try {
        val response = REQUEST("GET", url, js("{ cache: 'file' }"))
        return response.getBody(encoding)
    } catch (e: Throwable) {
        throw IOException(e.message, e)
    }
}

// private fun toByteArray(obj: dynamic): ByteArray =
//     ByteArray(obj.length) { obj[it] }

// private fun Uint8Array.toByteArray(): ByteArray =
//     ByteArray(length) { this[it] }

// private fun ArrayBuffer.toUInt8Array(): Uint8Array =
//     Uint8Array(this)

// private fun ArrayBuffer.toByteArray(): ByteArray =
//     toUInt8Array().toByteArray()

private fun browserReadText(path: String): String =
    js("window").localStorage.getItem(path) as? String
        ?: throw IOException("No such entry in window.localStorage: $path")

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

// @Suppress("UNUSED_PARAMETER")
// private fun browserReadBin(path: String): ByteArray =
//     throw IOException("Reading a local file in browser is not supported, yet")

// private fun nodeReadBin(path: String): ByteArray =
//     try {
//         toByteArray(FS.readFileSync(path, READ_BINARY))
//     } catch (e: Throwable) {
//         throw IOException("Error while reading file $path", e)
//     }

// fun readBin(path: String): ByteArray =
//     if (Info.PLATFORM == Platform.BROWSER) {
//         browserReadBin(path)
//     } else {
//         nodeReadBin(path)
//     }
