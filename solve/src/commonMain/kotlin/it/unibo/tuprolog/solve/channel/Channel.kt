package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

interface Channel<T> {
    @JsName("addListener")
    fun addListener(listener: Listener<T>)

    @JsName("removeListener")
    fun removeListener(listener: Listener<T>)

    @JsName("clearListeners")
    fun clearListeners()

    @JsName("close")
    fun close()

    @JsName("isClosed")
    val isClosed: Boolean

    @JsName("streamTerm")
    val streamTerm: Struct
}
