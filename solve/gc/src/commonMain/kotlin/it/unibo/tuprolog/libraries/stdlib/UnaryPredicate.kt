package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solve

abstract class UnaryPredicate(operator: String) : PrimitiveWrapper(operator, 1, false) {
    protected open fun computeSingleResponse(request: Solve.Request): Solve.Response = TODO()
}