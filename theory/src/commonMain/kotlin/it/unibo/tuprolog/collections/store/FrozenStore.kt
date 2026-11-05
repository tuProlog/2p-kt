package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/**
 * An immutable, read-optimised clause store: clauses in an array (global order) and grouped into
 * [FrozenProcedure]s. Updates return new stores sharing every procedure they do not touch.
 */
internal class FrozenStore private constructor(
    override val unificator: Unificator,
    val clauses: Array<Clause>,
    private val procedures: Map<ProcedureKey?, FrozenProcedure>,
) : ClauseSource {
    override val size: Int get() = clauses.size

    override fun ordered(): Sequence<Clause> = clauses.asSequence()

    override fun matching(query: Clause): Sequence<Clause> =
        procedures[procedureKeyOf(query)]?.matching(query) ?: emptySequence()

    override fun candidates(query: Clause): Sequence<Clause> =
        procedures[procedureKeyOf(query)]?.keyMatching(query) ?: emptySequence()

    /** A store with [added] (already validated) at the beginning or end. */
    fun plus(
        added: List<Clause>,
        atStart: Boolean = false,
    ): FrozenStore {
        if (added.isEmpty()) return this
        val newClauses = if (atStart) (added + clauses).toTypedArray() else (clauses.asList() + added).toTypedArray()
        val newProcedures = HashMap(procedures)
        for ((key, group) in added.groupBy { procedureKeyOf(it) }) {
            newProcedures[key] =
                procedures[key]?.plus(group, atStart) ?: FrozenProcedure(group.toTypedArray(), unificator)
        }
        return FrozenStore(unificator, newClauses, newProcedures)
    }

    /** The store without the first clause matching [query], and that clause; `null` if no clause matches. */
    fun minusFirst(query: Clause): Pair<FrozenStore, Clause>? {
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val matches = procedure.matches(query)
        if (!matches.hasNext()) return null
        val position = matches.nextInt()
        return minus(key, procedure, intArrayOf(position)) to procedure.clauses[position]
    }

    /** The store without the clauses matching [query], and those clauses; `null` if no clause matches. */
    fun minusAll(query: Clause): Pair<FrozenStore, List<Clause>>? {
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val positions = mutableListOf<Int>()
        procedure.matches(query).forEach { positions.add(it) }
        if (positions.isEmpty()) return null
        return minus(key, procedure, positions.toIntArray()) to positions.map { procedure.clauses[it] }
    }

    private fun minus(
        key: ProcedureKey?,
        procedure: FrozenProcedure,
        removed: IntArray,
    ): FrozenStore {
        val newClauses = ArrayList<Clause>(clauses.size - removed.size)
        // the procedure's clauses appear in the global array in the same order: walk both together
        var cursor = 0
        var next = 0
        for (clause in clauses) {
            if (cursor < procedure.size && clause === procedure.clauses[cursor]) {
                val isRemoved = next < removed.size && removed[next] == cursor
                cursor++
                if (isRemoved) {
                    next++
                    continue
                }
            }
            newClauses.add(clause)
        }
        val newProcedures = HashMap(procedures)
        if (procedure.size == removed.size) newProcedures.remove(key) else newProcedures[key] = procedure.minus(removed)
        return FrozenStore(unificator, newClauses.toTypedArray(), newProcedures)
    }

    companion object {
        fun empty(unificator: Unificator): FrozenStore = FrozenStore(unificator, emptyArray(), emptyMap())

        /** A store of [clauses], which must be already validated. */
        fun of(
            unificator: Unificator,
            clauses: List<Clause>,
        ): FrozenStore = empty(unificator).plus(clauses)
    }
}
