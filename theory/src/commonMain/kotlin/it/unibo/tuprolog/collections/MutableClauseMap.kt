package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ClauseMapImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseMap] that can be changed in place. */
interface MutableClauseMap<V> : ClauseMap<V> {
    /** Associates [value] to [key] (or to the variant of [key] already present), returning the previous value. */
    @JsName("put")
    fun put(
        key: Clause,
        value: V,
    ): V?

    /** Removes the entry whose key is a variant of [key], returning its value, or `null` if there is none. */
    @JsName("remove")
    fun remove(key: Clause): V?

    /** Removes the entries whose key unifies with [query], returning them. */
    @JsName("removeAllMatching")
    fun removeAllMatching(query: Clause): List<Pair<Clause, V>>

    /** Removes all entries. */
    @JsName("clear")
    fun clear()

    companion object {
        @JvmStatic
        @JsName("empty")
        fun <V> empty(unificator: Unificator = Unificator.default): MutableClauseMap<V> = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun <V> of(
            unificator: Unificator,
            vararg entries: Pair<Clause, V>,
        ): MutableClauseMap<V> = of(unificator, entries.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun <V> of(
            unificator: Unificator,
            entries: Iterable<Pair<Clause, V>>,
        ): MutableClauseMap<V> = ClauseMapImpl.of(unificator, entries)
    }
}
