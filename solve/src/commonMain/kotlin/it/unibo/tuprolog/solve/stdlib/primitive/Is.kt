package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.function.ArithmeticEvaluator
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * Implementation of 'is'/2 predicate
 *
 * @author Enrico
 */
object Is : BinaryRelation.Functional<ExecutionContext>("is") {
    private fun mgu(x: Term, y: Term): Substitution = x mguWith y

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        mgu(arguments[0], arguments[1].accept(ArithmeticEvaluator(context)))

}
