package it.unibo.tuprolog

import kotlinx.browser.window

@Suppress("TooGenericExceptionCaught", "SwallowedException", "SENSELESS_COMPARISON")
internal actual fun currentPlatform(): Platform =
    try {
        if (window === undefined) {
            Platform.NODE
        } else {
            Platform.BROWSER
        }
    } catch (e: Throwable) {
        Platform.NODE
    }

internal actual fun currentOs(): Os {
    val name =
        when (currentPlatform()) {
            Platform.NODE -> js("require('os').platform()") as String?
            Platform.BROWSER -> window.navigator.platform
            else -> null
        }
    return name?.let(Os.Companion::detect) ?: error("Cannot determine current OS: $name")
}
