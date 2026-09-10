package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.impl.InputChannelFromFunction
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [Channel] a [it.unibo.tuprolog.solve.Solver] reads [T]-typed elements from, e.g. backing Prolog's `read/1` or
 * a solver's standard input.
 *
 * Build one via the companion's [stdIn] (platform default standard input), [of] (wrapping a generator function or a
 * fixed [String]).
 */
interface InputChannel<T : Any> : Channel<T> {
    companion object {
        /** The platform-specific standard input channel (`System.in` on the JVM; unsupported on JS, see `Channels.kt`). */
        @JvmStatic
        @JsName("stdIn")
        fun stdIn(): InputChannel<String> = stdin()

        /**
         * Creates an [InputChannel] producing elements from [generator] (returning `null` when exhausted), whose
         * [available] reflects [availabilityChecker].
         */
        @JvmStatic
        @JsName("ofWithAvailabilityChecker")
        fun <X : Any> of(
            generator: () -> X?,
            availabilityChecker: () -> Boolean,
        ): InputChannel<X> = InputChannelFromFunction(generator, availabilityChecker)

        /** Same as the two-argument [of], but always reports [available] as `true`. */
        @JvmStatic
        @JsName("of")
        fun <X : Any> of(generator: () -> X?): InputChannel<X> = InputChannelFromFunction(generator, { true })

        /** Creates an [InputChannel] that yields [string], one character at a time. */
        @JvmStatic
        @JsName("ofString")
        fun of(string: String): InputChannel<String> = stringInputChannel(string)

        /** Builds the `$stream(in, Id)` [Struct] term identifying an input channel, given its optional [id]. */
        @JvmStatic
        @JvmOverloads
        @JsName("streamTerm")
        fun streamTerm(id: String? = null): Struct = Channel.streamTerm(input = true, id)
    }

    /**
     * Whether an element is currently available to be [read] without blocking (implementation-dependent; may throw
     * if the channel is closed, see [it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel]).
     */
    @JsName("available")
    val available: Boolean

    /** Whether this channel is exhausted, i.e. the next [read] would return `null`. */
    @JsName("isOver")
    val isOver: Boolean

    /** Reads and consumes the next element from this channel, or `null` if [isOver]. */
    @JsName("read")
    fun read(): T?

    /** Reads, without consuming, the next element from this channel, or `null` if [isOver]. */
    @JsName("peek")
    fun peek(): T?

    /** Runs [function] against this channel, [close]ing it once [function] returns normally (not on exceptions). */
    @JsName("use")
    fun <R> use(function: InputChannel<T>.() -> R): R = this.function().also { close() }
}
