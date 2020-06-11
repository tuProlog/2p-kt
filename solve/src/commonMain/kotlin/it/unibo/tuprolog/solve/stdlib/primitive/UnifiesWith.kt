package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** Implementation of '='/2 predicate */
object UnifiesWith : BinaryRelation.Predicative<ExecutionContext>("=") {
    override fun Solve.Request<ExecutionContext>.compute(first: Term, second: Term): Boolean =
        first matches second
}
