package it.unibo.tuprolog.solve.concurrent

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface AtomicInt {
    @JsName("value")
    var value: Int

    @JsName("plusAssign")
    operator fun plusAssign(delta: Int)

    @JsName("minusAssign")
    operator fun minusAssign(delta: Int)

    @JsName("incAndGet")
    fun incAndGet(delta: Int = 1): Int

    @JsName("decAndGet")
    fun decAndGet(delta: Int = 1): Int

    companion object {
        @JsName("zero")
        @JvmStatic
        fun zero(): AtomicInt = atomicInt(0)

        @JsName("of")
        @JvmStatic
        fun of(value: Int): AtomicInt = atomicInt(value)
    }
}

internal expect fun atomicInt(value: Int): AtomicInt
