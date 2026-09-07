package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

/**
 * An immutable map of [Channel]s keyed by alias, allowing a [it.unibo.tuprolog.solve.Solver] to hold several named
 * I/O channels at once (e.g. to support Prolog's `stream` terms for `format/3`-style I/O redirection), rather than
 * assuming a single channel per direction.
 *
 * Every mutating-looking operation ([plus]/[minus]/[setCurrent]/[close]) returns a new [Self] instance rather than
 * altering the receiver, consistent with the rest of 2P-Kt's immutable data structures. See [InputStore] and
 * [OutputStore] for the two concrete specializations used by [it.unibo.tuprolog.solve.Solver].
 *
 * @param T the type of element carried by the stored channels.
 * @param C the concrete [Channel] subtype stored.
 * @param Self the concrete [ChannelStore] subtype, for covariant-returning methods.
 */
interface ChannelStore<T : Any, C : Channel<T>, Self : ChannelStore<T, C, Self>> : Map<String, C> {
    companion object {
        /** The reserved alias under which the [current] channel, if any, is looked up. */
        const val CURRENT = "\$current"
    }

    /** The channel aliased as [CURRENT] in this store, or `null` if none is set. */
    @JsName("current")
    val current: C?
        get() = this[CURRENT]

    /** Every alias, other than [CURRENT] itself, under which [current] is also reachable. */
    @JsName("currentAliases")
    val currentAliases: Sequence<String>
        get() = current?.let { aliasesOf(it) } ?: emptySequence()

    /** Returns a copy of this store with [CURRENT] pointing to the channel already aliased as [alias]. */
    @JsName("setCurrentAlias")
    fun setCurrent(alias: String): Self

    /** Returns a copy of this store with [CURRENT] pointing to [channel]. */
    @JsName("setCurrentChannel")
    fun setCurrent(channel: C): Self

    /** Returns every channel in this store whose [Channel.streamTerm] unifies with [streamTerm]. */
    @JsName("findByTerm")
    fun findByTerm(streamTerm: Term): Sequence<C>

    /** Returns every alias under which [channel] is reachable in this store, excluding [CURRENT]. */
    @JsName("aliasesOf")
    fun aliasesOf(channel: C): Sequence<String>

    /** Returns a copy of this store with every entry of [others] added (overriding same-named existing ones). */
    @JsName("plusMap")
    operator fun plus(others: Map<String, C>): Self

    /** Returns a copy of this store with [other] added (overriding an existing entry with the same alias). */
    @JsName("plus")
    operator fun plus(other: Pair<String, C>): Self = plus(mapOf(other))

    /** Returns a copy of this store with every entry of [others] added (overriding same-named existing ones). */
    @JsName("plusIterable")
    operator fun plus(others: Iterable<Pair<String, C>>): Self = plus(others.toMap())

    /** Returns a copy of this store with every entry of [others] added (overriding same-named existing ones). */
    @JsName("plusSequence")
    operator fun plus(others: Sequence<Pair<String, C>>): Self = plus(others.toMap())

    /** Returns a copy of this store with [first] and [others] added (overriding same-named existing entries). */
    @JsName("plusMany")
    fun plus(
        first: Pair<String, C>,
        vararg others: Pair<String, C>,
    ): Self = plus(mapOf(first, *others))

    /** Returns a copy of this store without the entry aliased [other] (if any). */
    @JsName("minus")
    operator fun minus(other: String): Self = minus(sequenceOf(other))

    /** Returns a copy of this store without the entries aliased in [others]. */
    @JsName("minusIterable")
    operator fun minus(others: Iterable<String>): Self = minus(others.asSequence())

    /** Returns a copy of this store without the entries aliased in [others]. */
    @JsName("minusSequence")
    operator fun minus(others: Sequence<String>): Self

    /** Returns a copy of this store without the entries aliased [other] or in [others]. */
    @JsName("minusMany")
    fun minus(
        other: String,
        vararg others: String,
    ): Self = minus(sequenceOf(other, *others))

    /** [Channel.close]s [channel] and returns a copy of this store without any alias pointing to it. */
    @JsName("close")
    fun close(channel: C): Self = this - aliasesOf(channel.also { it.close() })
}
