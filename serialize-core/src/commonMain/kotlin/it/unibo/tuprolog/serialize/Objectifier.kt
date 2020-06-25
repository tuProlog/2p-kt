package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Objectifier<T, U> {
    @JsName("objectify")
    fun objectify(value: T): U
}