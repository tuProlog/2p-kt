package it.unibo.tuprolog.ui.gui

import kotlinx.browser.window

open class DefaultJsRunner : Runner {
    override fun ui(action: () -> Unit) {
        window.setTimeout(action)
    }

    override fun background(action: () -> Unit) {
        window.setTimeout(action)
    }

    override fun io(action: () -> Unit) {
        window.setTimeout(action)
    }
}
