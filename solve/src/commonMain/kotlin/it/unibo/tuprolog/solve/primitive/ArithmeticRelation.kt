package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.ArithmeticEvaluator

/** Base class for implementing arithmetic relation between [Numeric] terms */
abstract class ArithmeticRelation<E : ExecutionContext>(operator: String) : BinaryRelation.Predicative<E>(operator) {

    final override fun Solve.Request<E>.compute(first: Term, second: Term): Boolean {
        ensuringAllArgumentsAreInstantiated()
        return evaluateAndCompute(first, second)
    }

    private fun Solve.Request<E>.evaluateAndCompute(x: Term, y: Term): Boolean =
        ArithmeticEvaluator(context).let {
            computeNumeric(x.accept(it) as Numeric, y.accept(it) as Numeric)
        }

    abstract fun computeNumeric(x: Numeric, y: Numeric): Boolean
}
