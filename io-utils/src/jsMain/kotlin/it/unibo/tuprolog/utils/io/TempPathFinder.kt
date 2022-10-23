package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.currentPlatform

internal actual object TempPathFinder {
    private val os by lazy {
        js("require('os')")
    }

    actual val tempDirectory: String
        get() = if (currentPlatform() == Platform.NODE) {
            os.tmpdir().toString()
        } else {
            "/tmp"
        }

    actual fun file(name: String, extension: String): String =
        "$tempDirectory/$name.$extension"

    actual fun directory(name: String): String =
        "$tempDirectory/$name"
}
