package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputChannelFromFunction
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface OutputChannel<T> : Channel<T> {
    companion object {
        @JvmStatic
        @JsName("stdOut")
        fun <T> stdOut(): OutputChannel<T> = stdout()

        @JvmStatic
        @JsName("stdErr")
        fun <T> stdErr(): OutputChannel<T> = stderr()

        @JvmStatic
        @JsName("of")
        fun <T> of(consumer: (T) -> Unit): OutputChannel<T> = OutputChannelFromFunction(consumer)
    }

    @JsName("write")
    fun write(value: T)
}