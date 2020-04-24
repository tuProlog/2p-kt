package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputChannelFromFunction
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface InputChannel<T> : Channel<T> {
    companion object {

        @JvmStatic
        @JsName("stdIn")
        fun stdIn(): InputChannel<String> = stdin()

        @JvmStatic
        @JsName("ofWithAvailabilityChecker")
        fun <T> of(generator: () -> T, availabilityChecker: () -> Boolean): InputChannel<T> =
            InputChannelFromFunction(generator, availabilityChecker)

        @JvmStatic
        @JsName("of")
        fun <T> of(generator: () -> T): InputChannel<T> =
            InputChannelFromFunction(generator, { true })
    }

    @JsName("available")
    val available: Boolean

    @JsName("read")
    fun read(): T
}