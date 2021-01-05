package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputChannelFromFunction
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface InputChannel<T : Any> : Channel<T> {
    companion object {
        @JvmStatic
        @JsName("stdIn")
        fun stdIn(): InputChannel<String> = stdin()

        @JvmStatic
        @JsName("ofWithAvailabilityChecker")
        fun <X : Any> of(generator: () -> X?, availabilityChecker: () -> Boolean): InputChannel<X> =
            InputChannelFromFunction(generator, availabilityChecker)

        @JvmStatic
        @JsName("of")
        fun <X : Any> of(generator: () -> X?): InputChannel<X> =
            InputChannelFromFunction(generator, { true })
    }

    @JsName("available")
    val available: Boolean

    @JsName("isOver")
    val isOver: Boolean

    @JsName("read")
    fun read(): T?
}
