package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

/** A procedure: rules (and facts) sharing head functor and arity. */
internal data class ProcedureKey(
    val functor: String,
    val arity: Int,
)

/** The key of the procedure [clause] belongs to, `null` for directives. */
internal fun procedureKeyOf(clause: Clause): ProcedureKey? = clause.head?.let { ProcedureKey(it.functor, it.arity) }
