package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Channel<T : Any> {
    @JsName("addListener")
    fun addListener(listener: Listener<T?>)

    @JsName("removeListener")
    fun removeListener(listener: Listener<T?>)

    @JsName("clearListeners")
    fun clearListeners()

    @JsName("close")
    fun close()

    @JsName("isClosed")
    val isClosed: Boolean

    @JsName("streamTerm")
    val streamTerm: Struct

    companion object {
        @JvmStatic
        @JsName("streamTerm")
        fun streamTerm(input: Boolean? = null, id: String? = null): Struct =
            Struct.of(
                "\$stream",
                input?.let { if (it) "in" else "out" }?.let { Atom.of(it) } ?: Var.anonymous(),
                id?.let { Atom.of(it) } ?: Var.anonymous()
            )
    }
}
