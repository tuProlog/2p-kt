package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Tuple

internal class WhenTuple<T>(
    private val ifTuple: (Tuple) -> T,
    private val otherwise: (Term) -> T
) : TermVisitor<T> {
    override fun defaultValue(term: Term): T = otherwise(term)
    override fun visitTuple(term: Tuple): T = ifTuple(term)
}
