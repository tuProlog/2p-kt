package it.unibo.tuprolog.solve.channel

import kotlin.js.JsName

interface Channel<T> {
    @JsName("addListener")
    fun addListener(listener: Listener<T>)

    @JsName("removeListener")
    fun removeListener(listener: Listener<T>)

    @JsName("clearListeners")
    fun clearListeners()

}