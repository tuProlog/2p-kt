package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.impl.OutputChannelFromFunction
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [Channel] a [it.unibo.tuprolog.solve.Solver] writes [T]-typed elements to, e.g. backing Prolog's `write/1`, a
 * solver's standard output/error, or warning reporting.
 *
 * Build one via the companion's [stdOut]/[stdErr]/[warn] (platform defaults), or [of] (wrapping a consumer function).
 */
interface OutputChannel<T : Any> : Channel<T> {
    companion object {
        /** The platform-specific standard output channel (`System.out` on the JVM; `console.log`/`print` on JS). */
        @JvmStatic
        @JsName("stdOut")
        fun <X : Any> stdOut(): OutputChannel<X> = stdout()

        /** The platform-specific standard error channel (`System.err` on the JVM; `console.error` on JS). */
        @JvmStatic
        @JsName("stdErr")
        fun <X : Any> stdErr(): OutputChannel<X> = stderr()

        /** The platform-specific channel [it.unibo.tuprolog.solve.exception.Warning]s are reported to by default. */
        @JvmStatic
        @JsName("warning")
        fun warn(): OutputChannel<Warning> = warning()

        /** Creates an [OutputChannel] delegating every [write] to [consumer]. */
        @JvmStatic
        @JsName("of")
        fun <T : Any> of(consumer: (T) -> Unit): OutputChannel<T> = OutputChannelFromFunction(consumer)

        /** Builds the `$stream(out, Id)` [Struct] term identifying an output channel, given its optional [id]. */
        @JvmStatic
        @JvmOverloads
        @JsName("streamTerm")
        fun streamTerm(id: String? = null): Struct = Channel.streamTerm(input = false, id)
    }

    /** Runs [function] against this channel, [close]ing it once [function] returns normally (not on exceptions). */
    @JsName("use")
    fun <R> use(function: OutputChannel<T>.() -> R): R = this.function().also { close() }

    /**
     * Writes [value] to this channel, notifying every registered [Listener].
     * @throws IllegalStateException if this channel is closed (implementation-dependent, see
     * [it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel]).
     */
    @JsName("write")
    fun write(value: T)

    /**
     * Flushes any buffered output on this channel.
     * @throws IllegalStateException if this channel is closed (implementation-dependent, see
     * [it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel]).
     */
    @JsName("flush")
    fun flush()
}
