package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Objectifier<T> {
    @JsName("objectify")
    fun objectify(value: T): Any
}