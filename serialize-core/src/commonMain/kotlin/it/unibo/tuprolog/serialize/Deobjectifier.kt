package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Deobjectifier<T, U> {
    @JsName("deobjectify")
    fun deobjectify(`object`: U): T
}