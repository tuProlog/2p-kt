package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Term

abstract class ExhaustiveTermVisitor<T> : AbstractTermVisitor<T>() {
    override fun <X : Term> join(term: X, f1: (X) -> T, vararg fs: (X) -> T): T =
        sequenceOf(f1, *fs).map { it(term) }.last()
}