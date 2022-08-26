package it.unibo.tuprolog.utils.observe

import kotlin.js.JsName

fun interface Binding {
    @JsName("unbind")
    fun unbind()
}
