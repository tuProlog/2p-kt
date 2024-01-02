package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.impl.InputChannelFromFunction
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface InputChannel<T : Any> : Channel<T> {
    companion object {
        @JvmStatic
        @JsName("stdIn")
        fun stdIn(): InputChannel<String> = stdin()

        @JvmStatic
        @JsName("ofWithAvailabilityChecker")
        fun <X : Any> of(
            generator: () -> X?,
            availabilityChecker: () -> Boolean,
        ): InputChannel<X> = InputChannelFromFunction(generator, availabilityChecker)

        @JvmStatic
        @JsName("of")
        fun <X : Any> of(generator: () -> X?): InputChannel<X> = InputChannelFromFunction(generator, { true })

        @JvmStatic
        @JsName("ofString")
        fun of(string: String): InputChannel<String> = stringInputChannel(string)

        @JvmStatic
        @JvmOverloads
        @JsName("streamTerm")
        fun streamTerm(id: String? = null): Struct = Channel.streamTerm(input = true, id)
    }

    @JsName("available")
    val available: Boolean

    @JsName("isOver")
    val isOver: Boolean

    @JsName("read")
    fun read(): T?

    @JsName("peek")
    fun peek(): T?

    @JsName("use")
    fun <R> use(function: InputChannel<T>.() -> R): R = this.function().also { close() }
}
