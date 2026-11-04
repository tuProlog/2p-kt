package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseSet
import it.unibo.tuprolog.collections.MutableClauseMap
import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.isVariantOf
import it.unibo.tuprolog.unify.Unificator

/** A clause stored as a key, compared by identity: variants are told apart by the store, not by the value map. */
private class KeyRef(
    val clause: Clause,
) {
    override fun equals(other: Any?): Boolean = other is KeyRef && other.clause === clause

    override fun hashCode(): Int = clause.hashCode()
}

/** A [MutableClauseMap] whose keys live in a [MutableStore], so that lookups by unification use its indexes. */
internal class ClauseMapImpl<V>(
    override val unificator: Unificator,
) : MutableClauseMap<V> {
    private val store = MutableStore(unificator)
    private val values = HashMap<KeyRef, V>()

    override val size: Int get() = store.size

    override val keys: ClauseSet = MutableClauseSetImpl(store)

    @Suppress("UNCHECKED_CAST")
    private fun entryOf(key: Clause): Pair<Clause, V> = key to (values[KeyRef(key)] as V)

    override val entries: Sequence<Pair<Clause, V>> get() = store.ordered().map { entryOf(it) }

    private fun variantOf(key: Clause): Clause? = store.matching(key).firstOrNull { it.isVariantOf(key) }

    override fun get(key: Clause): V? = variantOf(key)?.let { values[KeyRef(it)] }

    override fun entriesMatching(query: Clause): Sequence<Pair<Clause, V>> = store.matching(query).map { entryOf(it) }

    override fun put(
        key: Clause,
        value: V,
    ): V? {
        val present = variantOf(key)
        if (present == null) {
            store.add(key)
            values[KeyRef(key)] = value
            return null
        }
        return values.put(KeyRef(present), value)
    }

    override fun remove(key: Clause): V? =
        store.removeFirst(key) { it.isVariantOf(key) }?.let { values.remove(KeyRef(it)) }

    @Suppress("UNCHECKED_CAST")
    override fun removeAllMatching(query: Clause): List<Pair<Clause, V>> =
        store.removeAll(query).map { it to (values.remove(KeyRef(it)) as V) }

    override fun clear() {
        store.clear()
        values.clear()
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is ClauseMapImpl<*> && size == other.size && asMap() == other.asMap())

    override fun hashCode(): Int = asMap().hashCode()

    override fun toString(): String = entries.joinToString(", ", "ClauseMap{", "}") { (k, v) -> "$k=$v" }

    companion object {
        fun <V> of(
            unificator: Unificator,
            entries: Iterable<Pair<Clause, V>>,
        ): ClauseMapImpl<V> = ClauseMapImpl<V>(unificator).also { map -> entries.forEach { (k, v) -> map.put(k, v) } }
    }
}
