package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.ui.gui.impl.EventImpl

interface Event<out T> {
    val name: String

    val event: T

    companion object {
        fun <T> of(name: String, event: T): Event<T> = EventImpl(name, event)
    }
}
