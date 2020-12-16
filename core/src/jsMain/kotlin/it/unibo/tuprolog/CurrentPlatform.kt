package it.unibo.tuprolog

import kotlinx.browser.window

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
