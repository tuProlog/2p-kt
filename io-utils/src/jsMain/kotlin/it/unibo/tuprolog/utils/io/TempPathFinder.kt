package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.Platform
import it.unibo.tuprolog.currentPlatform
import kotlinx.browser.window

internal actual object TempPathFinder {
    private val os by lazy {
        js("require('os')")
    }

    private val math by lazy {
        if (currentPlatform() == Platform.BROWSER) {
            window.asDynamic().Math
        } else {
            js("Math")
        }
    }

    private fun randomTag() = math.random().toString().replace(".", "")

    actual val tempDirectory: String
        get() = if (currentPlatform() == Platform.NODE) {
            os.tmpdir().toString()
        } else {
            "/tmp"
        }

    actual fun file(name: String, extension: String): String =
        "$tempDirectory/$name${randomTag()}.$extension"

    actual fun directory(name: String): String =
        "$tempDirectory/$name${randomTag()}"
}
