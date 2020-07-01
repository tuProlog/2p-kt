package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.BinaryRelation

/** Implementation of '@=<'/2 predicate */
object TermLowerThanOrEqualTo : BinaryRelation.Predicative<ExecutionContext>("@=<") {
    override fun Solve.Request<ExecutionContext>.compute(first: Term, second: Term): Boolean {
        return first <= second
    }
}
