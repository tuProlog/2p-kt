package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.impl.OutputChannelFromFunction
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface OutputChannel<T : Any> : Channel<T> {
    companion object {
        @JvmStatic
        @JsName("stdOut")
        fun <X : Any> stdOut(): OutputChannel<X> = stdout()

        @JvmStatic
        @JsName("stdErr")
        fun <X : Any> stdErr(): OutputChannel<X> = stderr()

        @JvmStatic
        @JsName("warning")
        fun warn(): OutputChannel<Warning> = warning()

        @JvmStatic
        @JsName("of")
        fun <T : Any> of(consumer: (T) -> Unit): OutputChannel<T> = OutputChannelFromFunction(consumer)

        @JvmStatic
        @JvmOverloads
        @JsName("streamTerm")
        fun streamTerm(id: String? = null): Struct =
            Channel.streamTerm(input = false, id)
    }

    @JsName("use")
    fun <R> use(function: OutputChannel<T>.() -> R): R =
        this.function().also { close() }

    @JsName("write")
    fun write(value: T)

    @JsName("flush")
    fun flush()
}
