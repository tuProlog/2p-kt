package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/**
 * An immutable, read-optimised clause store: clauses in an array (global order) and grouped into
 * [FrozenProcedure]s. Updates return new stores sharing every procedure they do not touch.
 *
 * A clause whose head is a [it.unibo.tuprolog.core.Pattern] (e.g. `P(X) :- Q(X)`) belongs to every procedure of its
 * arity, at its place in the global order, and to the procedure of [wildcards] for that arity, which serves lookups
 * for symbols having no procedure. So, lookups by concrete heads stay as they are, and only adding or removing
 * pattern-headed clauses pays for them. Lookups by pattern heads go through a procedure holding all the clauses of
 * their arity (see [byArity]), built on the first such lookup. Removals by concrete heads only remove clauses with
 * concrete heads.
 */
internal class FrozenStore private constructor(
    override val unificator: Unificator,
    val clauses: Array<Clause>,
    private val procedures: Map<ProcedureKey?, FrozenProcedure>,
    private val wildcards: Map<Int, FrozenProcedure>,
    private val customHeadKeys: Boolean,
    // the procedures of all the clauses of each arity, built by the first lookup by a pattern head of that arity
    private var byArity: Map<Int, FrozenProcedure>,
) : ClauseSource {
    override val size: Int get() = clauses.size

    override fun ordered(): Sequence<Clause> = clauses.asSequence()

    override fun matching(query: Clause): Sequence<Clause> =
        if (needsScan(query)) {
            clauses.asSequence().filter { unificator.match(query, it) }
        } else {
            procedureFor(query)?.matching(query) ?: emptySequence()
        }

    override fun candidates(query: Clause): Sequence<Clause> =
        if (needsScan(query)) {
            clauses.asSequence()
        } else {
            procedureFor(query)?.keyMatching(query)
                ?: emptySequence()
        }

    private fun needsScan(query: Clause): Boolean = customHeadKeys || unificator.hasCustomHeadKey(query)

    /** The procedure holding the candidates for [query]. */
    private fun procedureFor(query: Clause): FrozenProcedure? =
        if (query.hasPatternHead) {
            arityProcedure(query.head!!.arity)
        } else {
            procedures[procedureKeyOf(query)] ?: query.head?.let { wildcards[it.arity] }
        }

    private fun arityProcedure(arity: Int): FrozenProcedure =
        byArity[arity] ?: FrozenProcedure(clauses.filter { it.head?.arity == arity }.toTypedArray(), unificator).also {
            byArity = byArity + (arity to it)
        }

    /** A store with [added] (already validated) at the beginning or end. */
    fun plus(
        added: List<Clause>,
        atStart: Boolean = false,
    ): FrozenStore {
        if (added.isEmpty()) return this
        val newClauses = if (atStart) (added + clauses).toTypedArray() else (clauses.asList() + added).toTypedArray()
        val newProcedures = HashMap(procedures)
        val hasPatterns = added.any { it.hasPatternHead }
        for ((key, group) in groupByProcedure(added, hasPatterns)) {
            newProcedures[key] = procedures[key]?.plus(group, atStart) ?: newProcedure(key, group, atStart)
        }
        val newWildcards =
            if (!hasPatterns) {
                wildcards
            } else {
                HashMap(wildcards).also { result ->
                    for ((arity, group) in added.filter { it.hasPatternHead }.groupBy { it.head!!.arity }) {
                        result[arity] =
                            wildcards[arity]?.plus(group, atStart) ?: FrozenProcedure(group.toTypedArray(), unificator)
                    }
                }
            }
        val newByArity =
            if (byArity.isEmpty()) {
                byArity
            } else {
                byArity.mapValues { (arity, procedure) ->
                    val group = added.filter { it.head?.arity == arity }
                    if (group.isEmpty()) procedure else procedure.plus(group, atStart)
                }
            }
        return FrozenStore(
            unificator,
            newClauses,
            newProcedures,
            newWildcards,
            customHeadKeys || added.any { unificator.hasCustomHeadKey(it) },
            newByArity,
        )
    }

    /**
     * The clauses of [added] to add to each procedure, in order: a pattern-headed clause goes to every procedure of its
     * arity, existing or being created.
     */
    private fun groupByProcedure(
        added: List<Clause>,
        hasPatterns: Boolean,
    ): Map<ProcedureKey?, List<Clause>> {
        if (!hasPatterns) return added.groupBy { procedureKeyOf(it) }
        val groups = LinkedHashMap<ProcedureKey?, MutableList<Clause>>()
        for (clause in added) if (!clause.hasPatternHead) groups.getOrPut(procedureKeyOf(clause)) { mutableListOf() }
        for (key in procedures.keys) if (key != null) groups.getOrPut(key) { mutableListOf() }
        val keysByArity = groups.keys.filterNotNull().groupBy { it.arity }
        for (clause in added) {
            if (clause.hasPatternHead) {
                keysByArity[clause.head!!.arity]?.forEach { groups[it]!!.add(clause) }
            } else {
                groups[procedureKeyOf(clause)]!!.add(clause)
            }
        }
        return groups.filterValues { it.isNotEmpty() }
    }

    /** A new procedure for [key], holding the pattern-headed clauses of its arity stored so far, and [group]. */
    private fun newProcedure(
        key: ProcedureKey?,
        group: List<Clause>,
        atStart: Boolean,
    ): FrozenProcedure {
        val shared = key?.let { wildcards[it.arity] } ?: return FrozenProcedure(group.toTypedArray(), unificator)
        return shared.plus(group, atStart)
    }

    /** The store without the first clause matching [query], and that clause; `null` if no clause matches. */
    fun minusFirst(query: Clause): Pair<FrozenStore, Clause>? {
        if (needsScan(query)) {
            val position = clauses.indexOfFirst { unificator.match(query, it) }
            if (position < 0) return null
            return of(unificator, clauses.filterIndexed { i, _ -> i != position }) to clauses[position]
        }
        if (query.hasPatternHead) {
            val procedure = arityProcedure(query.head!!.arity)
            val matches = procedure.matches(query)
            if (!matches.hasNext()) return null
            val removed = procedure.clauses[matches.nextInt()]
            return minusClauses(listOf(removed)) to removed
        }
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val matches = procedure.concreteMatches(query)
        if (!matches.hasNext()) return null
        val position = matches.nextInt()
        return minus(key, procedure, intArrayOf(position)) to procedure.clauses[position]
    }

    /** The store without the clauses matching [query], and those clauses; `null` if no clause matches. */
    fun minusAll(query: Clause): Pair<FrozenStore, List<Clause>>? {
        if (needsScan(query)) {
            val (removed, kept) = clauses.partition { unificator.match(query, it) }
            return if (removed.isEmpty()) null else of(unificator, kept) to removed
        }
        if (query.hasPatternHead) {
            val procedure = arityProcedure(query.head!!.arity)
            val removed = mutableListOf<Clause>()
            procedure.matches(query).forEach { removed.add(procedure.clauses[it]) }
            return if (removed.isEmpty()) null else minusClauses(removed) to removed
        }
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val positions = mutableListOf<Int>()
        procedure.concreteMatches(query).forEach { positions.add(it) }
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
        val newByArity = if (key == null || byArity.isEmpty()) byArity else byArity - key.arity
        return FrozenStore(unificator, newClauses.toTypedArray(), newProcedures, wildcards, customHeadKeys, newByArity)
    }

    /**
     * The store without [removed], clauses of this store in their global order, whatever their heads: the global array,
     * and each procedure holding some of them, are filtered walking along both.
     */
    private fun minusClauses(removed: List<Clause>): FrozenStore {
        val patternArities = removed.filter { it.hasPatternHead }.mapTo(HashSet()) { it.head!!.arity }
        val affected = removed.filterNot { it.hasPatternHead }.mapTo(HashSet()) { procedureKeyOf(it) }
        if (patternArities.isNotEmpty()) procedures.keys.filterTo(affected) { it != null && it.arity in patternArities }
        val newProcedures = HashMap(procedures)
        for (key in affected) {
            val own =
                removed.filter {
                    if (it.hasPatternHead) key != null && key.arity == it.head!!.arity else procedureKeyOf(it) == key
                }
            val kept = procedures[key]!!.clauses.without(own)
            if (kept.isEmpty()) newProcedures.remove(key) else newProcedures[key] = FrozenProcedure(kept, unificator)
        }
        val newWildcards =
            if (patternArities.isEmpty()) {
                wildcards
            } else {
                HashMap(wildcards).also { result ->
                    for (arity in patternArities) {
                        val own = removed.filter { it.hasPatternHead && it.head!!.arity == arity }
                        val kept = wildcards[arity]!!.clauses.without(own)
                        if (kept.isEmpty()) result.remove(arity) else result[arity] = FrozenProcedure(kept, unificator)
                    }
                }
            }
        return FrozenStore(
            unificator,
            clauses.without(removed),
            newProcedures,
            newWildcards,
            customHeadKeys,
            byArity - removed.mapNotNullTo(HashSet()) { it.head?.arity },
        )
    }

    /** Positions of the clauses with a concrete head unifying with [query], which removals by concrete heads remove. */
    private fun FrozenProcedure.concreteMatches(query: Clause): IntIterator =
        if (wildcards.isEmpty()) matches(query) else MatchingPositions(matches(query), clauses) { !it.hasPatternHead }

    companion object {
        fun empty(unificator: Unificator): FrozenStore =
            FrozenStore(unificator, emptyArray(), emptyMap(), emptyMap(), false, emptyMap())

        /** A store of [clauses], which must be already validated. */
        fun of(
            unificator: Unificator,
            clauses: List<Clause>,
        ): FrozenStore = empty(unificator).plus(clauses)

        /** These clauses, without [removed]: a subsequence of them (by identity), in the same order. */
        private fun Array<Clause>.without(removed: List<Clause>): Array<Clause> {
            val result = ArrayList<Clause>(size - removed.size)
            var next = 0
            for (clause in this) {
                if (next < removed.size && clause === removed[next]) next++ else result.add(clause)
            }
            return result.toTypedArray()
        }
    }
}
