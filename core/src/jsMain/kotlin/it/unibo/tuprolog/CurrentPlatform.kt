package it.unibo.tuprolog

import kotlinx.browser.window

internal actual fun currentPlatform(): Platform =
    try {
        if (window === undefined) {
            Platform.BROWSER
        } else {
            Platform.NODE
        }
    } catch (e: Throwable) {
        Platform.NODE
    }
