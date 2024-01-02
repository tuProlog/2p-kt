package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Evaluates a [Term] as an expression, w.r.t. the loaded functions provided through [request].
 * Throws a [TypeError] in case a non-evaluable sub-term is met.
 *
 * @param request the request of the primitive in which the evaluation should happen
 * @param index the index of the argument being evalued in the aforementioned primitive
 */
class ExpressionEvaluator<E : ExecutionContext>(
    request: Solve.Request<E>,
    index: Int? = null,
) : AbstractEvaluator<E, Term>(request, index)
