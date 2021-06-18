package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var

internal data class WhenVar<T>(
    val ifVar: (Var) -> T,
    val otherwise: (Term) -> T
) : TermVisitor<T> {
    override fun defaultValue(term: Term): T = otherwise(term)
    override fun visitVar(term: Var): T = ifVar(term)
}
