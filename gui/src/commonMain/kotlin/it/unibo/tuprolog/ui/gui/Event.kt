package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.ui.gui.impl.EventImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Event<out T> {
    @JsName("name")
    val name: String

    @JsName("event")
    val event: T

    companion object {
        @JsName("of")
        @JvmStatic
        fun <T> of(name: String, event: T): Event<T> = EventImpl(name, event)
    }
}
