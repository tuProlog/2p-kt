package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.evalAsArithmeticExpression

/**
 * Base class for implementing ISO arithmetic comparison predicates (`=:=/2`, `=\=/2`, `</2`, `>/2`, `=</2`, `>=/2`),
 * i.e. [BinaryRelation.Predicative]s that first evaluate both arguments as arithmetic expressions (via
 * [it.unibo.tuprolog.solve.function.evalAsArithmeticExpression], raising an
 * [it.unibo.tuprolog.solve.exception.error.InstantiationError] if unbound, or a
 * [it.unibo.tuprolog.solve.exception.error.TypeError] if not evaluable/numeric) before comparing the resulting
 * [Numeric] values via [computeNumeric].
 */
abstract class ArithmeticRelation<E : ExecutionContext>(
    operator: String,
) : BinaryRelation.Predicative<E>(operator) {
    final override fun Solve.Request<E>.compute(
        first: Term,
        second: Term,
    ): Boolean {
        ensuringAllArgumentsAreInstantiated()
        return evaluateAndCompute(first, second)
    }

    private fun Solve.Request<E>.evaluateAndCompute(
        x: Term,
        y: Term,
    ): Boolean = computeNumeric(x.evalAsArithmeticExpression(this, 0), y.evalAsArithmeticExpression(this, 1))

    /** Template method comparing the two already-evaluated [Numeric] operands. */
    abstract fun computeNumeric(
        x: Numeric,
        y: Numeric,
    ): Boolean
}
