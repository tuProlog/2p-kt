package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseList
import it.unibo.tuprolog.collections.MutableClauseList
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal class MutableClauseListImpl(
    private val store: MutableStore,
) : StoreBackedCollection(),
    MutableClauseList {
    override val source: ClauseSource get() = store

    override fun add(clause: Clause): Boolean {
        store.add(clause)
        return true
    }

    override fun addFirst(clause: Clause) = store.add(clause, atStart = true)

    override fun removeFirst(): Clause? =
        store
            .entries()
            .firstOrNull()
            ?.also { store.remove(it) }
            ?.clause

    override fun removeLast(): Clause? = store.lastEntry()?.also { store.remove(it) }?.clause

    override fun removeFirstMatching(query: Clause): Clause? = store.removeFirst(query)

    override fun removeAllMatching(query: Clause): List<Clause> = store.removeAll(query)

    override fun clear() = store.clear()

    override fun lastOrNull(): Clause? = store.lastEntry()?.clause

    override fun asList(): List<Clause> = toList()

    private fun entryAt(index: Int) =
        (if (index >= 0) store.entries().elementAtOrNull(index) else null)
            ?: throw IndexOutOfBoundsException("Index $index out of bounds for size $size")

    override fun get(index: Int): Clause = entryAt(index).clause

    // shortcut: positional insertion rebuilds the store (O(n)); add gap-based order keys if it ever gets hot
    private fun rebuild(clauses: List<Clause>) {
        store.clear()
        store.addAll(clauses)
    }

    override fun add(
        index: Int,
        clause: Clause,
    ) = rebuild(asList().toMutableList().apply { add(index, clause) })

    override fun set(
        index: Int,
        clause: Clause,
    ): Clause {
        val clauses = asList().toMutableList()
        val replaced = clauses.set(index, clause)
        rebuild(clauses)
        return replaced
    }

    override fun removeAt(index: Int): Clause = entryAt(index).also { store.remove(it) }.clause

    override fun toMutable(): MutableClauseList = of(unificator, asList())

    override fun toImmutable(): ClauseList = FrozenClauseList.of(unificator, asList())

    override fun equals(other: Any?): Boolean = orderedEquals(other)

    override fun hashCode(): Int = orderedHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseListImpl = MutableClauseListImpl(MutableStore(unificator).also { it.addAll(clauses.toList()) })
    }
}
