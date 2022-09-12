package it.unibo.tuprolog.utils.observe

import kotlin.js.JsName

fun interface Observer<in T> {
    @JsName("notify")
    fun notify(value: T)
}
