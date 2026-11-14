package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/** A procedure: rules (and facts) sharing head functor and arity. */
internal data class ProcedureKey(
    val functor: String,
    val arity: Int,
)

/** The key of the procedure [clause] belongs to, `null` for directives. */
internal fun procedureKeyOf(clause: Clause): ProcedureKey? =
    clause.concreteHead()?.let { ProcedureKey(it.functor, it.arity) }

/**
 * The head of [this] clause, which must have a concrete functor, or `null` for directives.
 * shortcut: Pattern heads (stored or queried) throw until procedures support variable functors (wildcard buckets).
 */
internal fun Clause.concreteHead(): Struct? =
    head?.let {
        it.asStruct() ?: throw UnsupportedOperationException("Clauses whose head has a variable functor: $this")
    }

// shortcut: custom head keys use store scans; add head-key indexes if these lookups become hot.
internal fun Unificator.hasCustomHeadKey(clause: Clause): Boolean =
    clause.head?.let { indexKey(it) != IndexKey.strict(it) } ?: false
