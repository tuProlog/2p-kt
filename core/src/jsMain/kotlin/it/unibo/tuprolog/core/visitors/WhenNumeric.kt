package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal class WhenNumeric<T>(
    private val ifNumeric: (Numeric) -> T,
    private val ifInteger: (Integer) -> T,
    private val ifReal: (Real) -> T,
    private val otherwise: (Term) -> T
) : TermVisitor<T> {

    override fun visitNumeric(term: Numeric): T = ifNumeric(term)

    override fun visitInteger(term: Integer): T = ifInteger(term)

    override fun visitReal(term: Real): T = ifReal(term)

    override fun defaultValue(term: Term): T = otherwise(term)
}
