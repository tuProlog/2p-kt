package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Base type abstracting a channel of communication of [T]-typed elements between a [it.unibo.tuprolog.solve.Solver]
 * and the outside world, without committing to a specific transport.
 *
 * This is what lets Prolog built-ins like `write/1`, `read/1`, or warning reporting talk to the outside world
 * uniformly whether the solver runs on the JVM (wrapping a `java.io.PrintStream`/`Reader`/`Writer`) or on JS
 * (wrapping `console.log`), since a built-in only ever depends on this abstraction, never on a concrete transport.
 * See [InputChannel] and [OutputChannel] for the two directions of communication, and
 * [it.unibo.tuprolog.solve.channel.ChannelStore] for how several named channels are held together by a solver.
 *
 * Every element transiting the channel (via [InputChannel.read]/[OutputChannel.write]) is broadcast to every
 * registered [Listener].
 */
interface Channel<T : Any> {
    /** Registers [listener] to be invoked with every element subsequently transiting this channel. */
    @JsName("addListener")
    fun addListener(listener: Listener<T?>)

    /** Unregisters [listener], previously added via [addListener]. */
    @JsName("removeListener")
    fun removeListener(listener: Listener<T?>)

    /** Unregisters every currently-registered [Listener]. */
    @JsName("clearListeners")
    fun clearListeners()

    /**
     * Closes this channel; further reads/writes on it are expected to fail.
     * @throws IllegalStateException if this channel is already closed (implementation-dependent, see [it.unibo.tuprolog.solve.channel.impl.AbstractChannel]).
     */
    @JsName("close")
    fun close()

    /** Whether this channel has been [close]d. */
    @JsName("isClosed")
    val isClosed: Boolean

    /** The Prolog `$stream(Direction, Id)`-like [Struct] term identifying this channel, e.g. for `current_output/1`. */
    @JsName("streamTerm")
    val streamTerm: Struct

    companion object {
        /** Builds the `$stream(Direction, Id)` [Struct] term identifying a channel, given its [input] direction (`in`/`out`, or an unbound variable if `null`) and [id] (or an unbound variable if `null`). */
        @JvmStatic
        @JsName("streamTerm")
        fun streamTerm(
            input: Boolean? = null,
            id: String? = null,
        ): Struct =
            Struct.of(
                "\$stream",
                input?.let { if (it) "in" else "out" }?.let { Atom.of(it) } ?: Var.anonymous(),
                id?.let { Atom.of(it) } ?: Var.anonymous(),
            )
    }
}
