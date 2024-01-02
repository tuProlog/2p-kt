package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

interface ChannelStore<T : Any, C : Channel<T>, Self : ChannelStore<T, C, Self>> : Map<String, C> {
    companion object {
        const val CURRENT = "\$current"
    }

    @JsName("current")
    val current: C?
        get() = this[CURRENT]

    @JsName("currentAliases")
    val currentAliases: Sequence<String>
        get() = current?.let { aliasesOf(it) } ?: emptySequence()

    @JsName("setCurrentAlias")
    fun setCurrent(alias: String): Self

    @JsName("setCurrentChannel")
    fun setCurrent(channel: C): Self

    @JsName("findByTerm")
    fun findByTerm(streamTerm: Term): Sequence<C>

    @JsName("aliasesOf")
    fun aliasesOf(channel: C): Sequence<String>

    @JsName("plusMap")
    operator fun plus(others: Map<String, C>): Self

    @JsName("plus")
    operator fun plus(other: Pair<String, C>): Self = plus(mapOf(other))

    @JsName("plusIterable")
    operator fun plus(others: Iterable<Pair<String, C>>): Self = plus(others.toMap())

    @JsName("plusSequence")
    operator fun plus(others: Sequence<Pair<String, C>>): Self = plus(others.toMap())

    @JsName("plusMany")
    fun plus(
        first: Pair<String, C>,
        vararg others: Pair<String, C>,
    ): Self = plus(mapOf(first, *others))

    @JsName("minus")
    operator fun minus(other: String): Self = minus(sequenceOf(other))

    @JsName("minusIterable")
    operator fun minus(others: Iterable<String>): Self = minus(others.asSequence())

    @JsName("minusSequence")
    operator fun minus(others: Sequence<String>): Self

    @JsName("minusMany")
    fun minus(
        other: String,
        vararg others: String,
    ): Self = minus(sequenceOf(other, *others))

    @JsName("close")
    fun close(channel: C): Self = this - aliasesOf(channel.also { it.close() })
}
