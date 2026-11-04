package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseList
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.ClauseSet
import it.unibo.tuprolog.collections.MutableClauseList
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseSet
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/*
 * Clause collections (#1019) are thin façades over the clause stores: a FrozenStore for immutable ones, a MutableStore
 * for mutable ones. Ordered and unordered collections share the stores (unordered ones simply promise no order).
 */

internal abstract class StoreBackedCollection : ClauseCollection {
    protected abstract val source: ClauseSource

    override val unificator: Unificator get() = source.unificator

    override val size: Long get() = source.size.toLong()

    override val isEmpty: Boolean get() = source.size == 0

    override fun iterator(): Iterator<Clause> = source.ordered().iterator()

    override fun get(clause: Clause): Sequence<Clause> = source.matching(clause)

    override fun toString(): String = joinToString(", ", "${this::class.simpleName}[", "]")
}

/** Ordered collections are equal when they hold equal clauses in the same order (theories only equal theories). */
private fun ClauseQueue.orderedEquals(other: Any?): Boolean =
    this === other ||
        (other is ClauseQueue && other !is Theory && size == other.size && zip(other).all { (a, b) -> a == b })

private fun ClauseQueue.orderedHashCode(): Int = fold(1) { hash, clause -> 31 * hash + clause.hashCode() }

/** Multisets are equal when they hold equal clauses with the same multiplicities. */
private fun ClauseMultiSet.multiSetEquals(other: Any?): Boolean =
    this === other ||
        (other is ClauseMultiSet && size == other.size && counts() == other.counts())

private fun ClauseMultiSet.counts(): Map<Clause, Int> = groupingBy { it }.eachCount()

private fun ClauseMultiSet.multiSetHashCode(): Int = sumOf { it.hashCode() }

internal class FrozenClauseList(
    private val store: FrozenStore,
) : StoreBackedCollection(),
    ClauseList {
    override val source: ClauseSource get() = store

    override fun asList(): List<Clause> = store.clauses.asList()

    override fun get(index: Int): Clause = store.clauses[index]

    override fun firstOrNull(): Clause? = store.clauses.firstOrNull()

    override fun lastOrNull(): Clause? = store.clauses.lastOrNull()

    override fun toMutable(): MutableClauseList = MutableClauseListImpl.of(unificator, asList())

    override fun toImmutable(): ClauseList = this

    override fun equals(other: Any?): Boolean = orderedEquals(other)

    override fun hashCode(): Int = orderedHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseList = FrozenClauseList(FrozenStore.of(unificator, clauses.toList()))
    }
}

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

internal open class FrozenClauseMultiSet(
    protected val store: FrozenStore,
) : StoreBackedCollection(),
    ClauseMultiSet {
    override val source: ClauseSource get() = store

    override fun toMutable(): MutableClauseMultiSet = MutableClauseMultiSetImpl.of(unificator, store.clauses.asList())

    override fun toImmutable(): ClauseMultiSet = this

    override fun equals(other: Any?): Boolean = multiSetEquals(other)

    override fun hashCode(): Int = multiSetHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseMultiSet = FrozenClauseMultiSet(FrozenStore.of(unificator, clauses.toList()))
    }
}

internal class FrozenClauseSet(
    store: FrozenStore,
) : FrozenClauseMultiSet(store),
    ClauseSet {
    override fun toMutable(): MutableClauseSet = MutableClauseSetImpl.of(unificator, store.clauses.asList())

    override fun toImmutable(): ClauseSet = this

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseSet =
            FrozenClauseSet(FrozenStore.of(unificator, MutableClauseSetImpl.of(unificator, clauses).toList()))
    }
}

internal open class MutableClauseMultiSetImpl(
    protected val store: MutableStore,
) : StoreBackedCollection(),
    MutableClauseMultiSet {
    override val source: ClauseSource get() = store

    override fun add(clause: Clause): Boolean {
        store.add(clause)
        return true
    }

    override fun removeFirstMatching(query: Clause): Clause? = store.removeFirst(query)

    override fun removeAllMatching(query: Clause): List<Clause> = store.removeAll(query)

    override fun clear() = store.clear()

    override fun toMutable(): MutableClauseMultiSet = of(unificator, toList())

    override fun toImmutable(): ClauseMultiSet = FrozenClauseMultiSet.of(unificator, toList())

    override fun equals(other: Any?): Boolean = multiSetEquals(other)

    override fun hashCode(): Int = multiSetHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseMultiSetImpl =
            MutableClauseMultiSetImpl(MutableStore(unificator).also { it.addAll(clauses.toList()) })
    }
}

internal class MutableClauseSetImpl(
    store: MutableStore,
) : MutableClauseMultiSetImpl(store),
    MutableClauseSet {
    override fun add(clause: Clause): Boolean = !containsVariant(clause) && super.add(clause)

    override fun toMutable(): MutableClauseSet = of(unificator, toList())

    override fun toImmutable(): ClauseSet = FrozenClauseSet(FrozenStore.of(unificator, toList()))

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseSetImpl = MutableClauseSetImpl(MutableStore(unificator)).also { it.addAll(clauses) }
    }
}
