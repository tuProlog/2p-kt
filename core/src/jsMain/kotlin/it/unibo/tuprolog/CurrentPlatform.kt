package it.unibo.tuprolog

import kotlinx.browser.window

internal actual fun currentPlatform(): Platform =
    if (window === undefined) {
        Platform.BROWSER
    } else {
        Platform.NODE
    }
