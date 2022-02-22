package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException

private val FS by lazy {
    js("require('fs')")
}

private val PATH by lazy {
    js("require('path')")
}

private val READ_TEXT = js("{ encoding: 'UTF-8', flag: 'r' }")

private val REQUEST: dynamic by lazy {
    js("require('sync-request')")
}

internal fun fetch(url: String, encoding: String): String {
    try {
        val response = REQUEST("GET", url, js("{}")) // js("{ cache: 'file' }"))
        return response.getBody(encoding)
    } catch (e: Throwable) {
        throw IOException(e.message, e)
    }
}

private fun browserReadText(path: String): String =
    js("window").localStorage.getItem(path) as? String
        ?: throw IOException("No such entry in window.localStorage: $path")

private fun String.toOsSpecificPath(): String {
    if (PATH.sep == "\\") {
        val rootFreePath = if (startsWith("/")) substring(1) else this
        return rootFreePath.replace("/", "\\")
    }
    return this
}

private fun nodeReadText(path: String): String =
    try {
        FS.readFileSync(path.toOsSpecificPath(), READ_TEXT).unsafeCast<String>()
    } catch (e: Throwable) {
        throw IOException("Error while reading file `$path`: ${e.message}", e)
    }

fun readText(path: String): String =
    if (Info.PLATFORM == Platform.BROWSER) {
        browserReadText(path)
    } else {
        nodeReadText(path)
    }
