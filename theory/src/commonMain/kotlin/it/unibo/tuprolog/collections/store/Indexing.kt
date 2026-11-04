package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
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

/** A procedure: rules (and facts) sharing head functor and arity. */
internal data class ProcedureKey(
    val functor: String,
    val arity: Int,
)

/** The key of the procedure [clause] belongs to, `null` for directives. */
internal fun procedureKeyOf(clause: Clause): ProcedureKey? = clause.head?.let { ProcedureKey(it.functor, it.arity) }

/** Procedures with fewer clauses than this are scanned: indexing them would cost more than it saves. */
internal const val INDEX_THRESHOLD = 8

/** Marks an argument whose index would contain no exact key at all, so as not to rebuild it at every lookup. */
internal object NotIndexable

/** The head of a query worth indexing, or `null` if the query cannot select candidates through argument indexes. */
internal fun Clause.indexableHead(): Struct? = head?.takeIf { it.arity > 0 }

/** What clause collections and theories read from a store. */
internal interface ClauseSource {
    val unificator: Unificator

    /** Number of clauses. */
    val size: Int

    /** All clauses, in order (for mutable stores, as of the call to `iterator()`). */
    fun ordered(): Sequence<Clause>

    /** The clauses unifying with [query], in order (for mutable stores, as of this call), lazily. */
    fun matching(query: Clause): Sequence<Clause>
}
