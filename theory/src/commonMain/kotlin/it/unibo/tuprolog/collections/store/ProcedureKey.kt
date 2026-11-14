package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/** A procedure: rules (and facts) sharing head functor and arity. */
internal data class ProcedureKey(
    val functor: String,
    val arity: Int,
)

/**
 * The key of the procedure [clause] belongs to, `null` for directives. Not meant for clauses whose head is a [Pattern]
 * (see [hasPatternHead]): they belong to every procedure of their arity.
 */
internal fun procedureKeyOf(clause: Clause): ProcedureKey? =
    clause.head?.let { ProcedureKey(it.castToStruct().functor, it.arity) }

/** Whether the head of this clause is a [Pattern], i.e. it has a variable functor. */
internal val Clause.hasPatternHead: Boolean
    get() = head is Pattern

// shortcut: custom head keys use store scans; add head-key indexes if these lookups become hot.
internal fun Unificator.hasCustomHeadKey(clause: Clause): Boolean =
    clause.head?.let { indexKey(it) != IndexKey.strict(it) } ?: false
