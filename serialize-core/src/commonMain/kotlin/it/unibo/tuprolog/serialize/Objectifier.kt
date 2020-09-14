package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Objectifier<T> {
    @JsName("objectify")
    fun objectify(value: T): Any

    @JsName("objectifyMany")
    fun objectifyMany(vararg values: T): Any =
        objectifyMany(listOf(*values))

    @JsName("objectifyManyIterable")
    fun objectifyMany(values: Iterable<T>): Any

    @JsName("objectifyManySequence")
    fun objectifyMany(values: Sequence<T>): Any =
        objectifyMany(values.asIterable())
}

