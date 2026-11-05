package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ClauseMapImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A read-only map from clauses to values, keyed by variant: two clauses that are variants of each other (see
 * [ClauseCollection.containsVariant]) are the same key. Besides lookups by key, [entriesMatching] retrieves, through
 * clause indexes, the entries whose key unifies with a query. Useful to attach data to clauses without changing their
 * tags: provenance, probabilities, statistics.
 *
 * Two maps are equal when they hold equal keys (by [Clause.equals], not by variant) associated with equal values.
 */
interface ClauseMap<out V> : UnificationAware {
    /** The number of entries (an `Int`, like `Map.size`, while clause collections count clauses with a `Long`). */
    @JsName("size")
    val size: Int

    /** Whether there are no entries. */
    @JsName("isEmpty")
    val isEmpty: Boolean get() = size == 0

    /** The keys, in insertion order: a read-only view, which reflects later changes to a mutable map. */
    @JsName("keys")
    val keys: ClauseSet

    /** The entries, in key insertion order. */
    @JsName("entries")
    val entries: Sequence<Pair<Clause, V>>

    /** The value associated with a variant of [key], or `null` if there is none. */
    @JsName("get")
    operator fun get(key: Clause): V?

    /** Whether some key is a variant of [key]. */
    @JsName("containsKey")
    fun containsKey(key: Clause): Boolean = keys.containsVariant(key)

    /** The entries whose key unifies with [query], lazily, in key insertion order. */
    @JsName("entriesMatching")
    fun entriesMatching(query: Clause): Sequence<Pair<Clause, V>>

    /** A read-only `Map` view (a snapshot, for mutable maps), whose keys are the very clauses stored. */
    @JsName("asMap")
    fun asMap(): Map<Clause, V> = entries.toMap()

    companion object {
        /** An empty [ClauseMap] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun <V> empty(unificator: Unificator = Unificator.default): ClauseMap<V> = of(unificator, emptyList())

        /**
         * A [ClauseMap] of the given entries, in order, matching clauses with [unificator]: an entry whose key is a
         * variant of an earlier one replaces its value, keeping the earlier key.
         */
        @JvmStatic
        @JsName("of")
        fun <V> of(
            unificator: Unificator,
            vararg entries: Pair<Clause, V>,
        ): ClauseMap<V> = of(unificator, entries.asIterable())

        /**
         * A [ClauseMap] of the given entries, in order, matching clauses with [unificator]: an entry whose key is a
         * variant of an earlier one replaces its value, keeping the earlier key.
         */
        @JvmStatic
        @JsName("ofIterable")
        fun <V> of(
            unificator: Unificator,
            entries: Iterable<Pair<Clause, V>>,
        ): ClauseMap<V> = ClauseMapImpl.of(unificator, entries)
    }
}
