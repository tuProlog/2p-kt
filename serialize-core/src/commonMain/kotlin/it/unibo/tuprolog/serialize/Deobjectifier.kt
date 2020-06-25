package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Deobjectifier<T> {
    @JsName("deobjectify")
    fun deobjectify(`object`: Any): T
}