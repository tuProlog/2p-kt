package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator
/*
 * Shared vocabulary of the clause stores (#1019).
 *
 * Clauses are grouped into procedures (same head functor and arity; directives form one more procedure, whose key is
 * `null`). Clause order only matters within a procedure for lookups, while the global order is kept separately for
 * whole-store iteration. Within a procedure, argument indexes are built on demand (just-in-time, as in YAP and
 * SWI-Prolog), on the first lookup that binds that argument; each lookup then uses the bound argument whose index
 * yields the fewest candidates. Candidates are the clauses whose key for that argument equals the query's, plus those
 * whose key is not exact (variables): both lists are sorted by clause order, and are merged lazily.
 */

/** Procedures with fewer clauses than this are scanned: indexing them would cost more than it saves. */
internal const val INDEX_THRESHOLD = 8

/** The head of a query worth indexing, or `null` if the query cannot select candidates through argument indexes. */
internal fun Clause.indexableHead(): Struct? = head?.takeIf { it.arity > 0 }

/** How deep indexes look along the chain of first arguments of compound arguments, as in `p(f(g(a)))`. */
internal const val MAX_INDEX_DEPTH = 4

/**
 * The key of the term found at [depth] along the chain of first arguments of [head]'s [argument] (depth 0 is the
 * argument itself). The chain stops, giving a wildcard, at anything this unificator does not key as a compound: e.g.
 * a variable, which unifies with whatever lies deeper.
 */
internal fun Unificator.nestedKey(
    head: Struct,
    argument: Int,
    depth: Int,
): IndexKey {
    var term = head.getArgAt(argument)
    repeat(depth) {
        val key = indexKey(term)
        if (key !is IndexKey.OfStruct || key.arity == 0 || !term.isStruct) return IndexKey.Wildcard
        term = term.castToStruct().getArgAt(0)
    }
    return indexKey(term)
}
