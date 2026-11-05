package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator
/*
 * Shared vocabulary of the clause stores (#1019).
 *
 * Clauses are grouped into procedures (same head functor and arity; directives form one more procedure, whose key is
 * `null`). Clause order only matters within a procedure for lookups, while the global order is kept separately for
 * whole-store iteration. Within a procedure, argument indexes are built on demand (just-in-time, as in YAP and
 * SWI-Prolog), on the first lookup that binds that argument (or a position within it, see searchPositions); each
 * lookup then uses the bound position whose index yields the fewest candidates. Candidates are the clauses whose key for that argument equals the query's, plus those
 * whose key is not exact (variables): both lists are sorted by clause order, and are merged lazily.
 */

/** Procedures with fewer clauses than this are scanned: indexing them would cost more than it saves. */
internal const val INDEX_THRESHOLD = 4

/** The head of a query worth indexing, or `null` if the query cannot select candidates through argument indexes. */
internal fun Clause.indexableHead(): Struct? = head?.takeIf { it.arity > 0 }

/** How deep indexes look into compound arguments: a head argument is at depth 1, its own arguments at depth 2, etc. */
internal const val MAX_INDEX_DEPTH = 4

/*
 * Indexed positions are paths: an argument of the head, then an argument of the compound found there, and so on, up to
 * MAX_INDEX_DEPTH steps. A path is encoded in an Int, 8 bits per step (the argument's index plus one), outermost
 * first: arguments beyond the 255th are not indexed.
 */
private const val STEP_BITS = 8
private const val STEP_MASK = 0xFF

/** The highest argument index a path can step into. */
internal const val MAX_STEP = STEP_MASK - 1

/** The path of the [argument]-th argument of the head. */
internal fun pathOf(argument: Int): Int = argument + 1

/** The path of the [argument]-th argument of the compound term at this path. */
internal fun Int.child(argument: Int): Int = (this shl STEP_BITS) or (argument + 1)

/** The argument indexes [path] steps through, outermost first: decode it once, then use [keyAt] as often as needed. */
internal fun stepsOf(path: Int): IntArray {
    var steps = 1
    while (steps < MAX_INDEX_DEPTH && (path ushr (STEP_BITS * steps)) != 0) steps++
    return IntArray(steps) { ((path ushr (STEP_BITS * (steps - 1 - it))) and STEP_MASK) - 1 }
}

/**
 * The key of the term found in [head] by following [steps] (see [stepsOf]). Paths stop, giving a wildcard, at anything
 * this unificator does not key as a compound (e.g. a variable, which unifies with whatever lies deeper) or lacking the
 * required argument.
 */
internal fun Unificator.keyAt(
    head: Struct,
    steps: IntArray,
): IndexKey {
    var term: Term = head
    for (i in steps.indices) {
        if (i > 0 && (indexKey(term) !is IndexKey.OfStruct || !term.isStruct)) return IndexKey.Wildcard
        val struct = term.castToStruct()
        if (steps[i] >= struct.arity) return IndexKey.Wildcard
        term = struct.getArgAt(steps[i])
    }
    return indexKey(term)
}

/**
 * Walks the positions of [head] (a query's) where an index could select candidates, i.e. where the query has an exact
 * key, calling [visit] with each position's path and key: the arguments of the head first, then, wherever [visit]
 * asks to, the arguments of the compound term at that position (as SWI-Prolog's deep indexing does when an argument's
 * index cannot tell clauses apart), down to [MAX_INDEX_DEPTH].
 */
internal fun Unificator.searchPositions(
    head: Struct,
    visit: (path: Int, key: IndexKey) -> Visit,
) {
    fun search(
        term: Term,
        path: Int,
        depth: Int,
    ): Boolean {
        val key = indexKey(term)
        if (!key.isExact) return true
        when (visit(path, key)) {
            Visit.STOP -> return false
            Visit.SKIP -> return true
            Visit.DESCEND -> Unit
        }
        if (depth >= MAX_INDEX_DEPTH || key !is IndexKey.OfStruct || !term.isStruct) return true
        val struct = term.castToStruct()
        for (i in 0 until minOf(struct.arity, MAX_STEP + 1)) {
            if (!search(struct.getArgAt(i), path.child(i), depth + 1)) return false
        }
        return true
    }
    for (i in 0 until minOf(head.arity, MAX_STEP + 1)) {
        if (!search(head.getArgAt(i), pathOf(i), 1)) return
    }
}
