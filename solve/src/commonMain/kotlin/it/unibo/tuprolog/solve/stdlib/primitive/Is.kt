package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.evalAsExpression
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implementation of 'is'/2 predicate
 *
 * @author Enrico
 */
object Is : BinaryRelation.Functional<ExecutionContext>("is") {

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        ensuringArgumentIsInstantiated(1).run {
            mgu(first, second.evalAsExpression(this, 1))
        }
}
