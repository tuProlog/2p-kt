package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * A class implementing a visitor that will evaluate expression terms according to context loaded functions
 *
 * No additional check is implemented at this level
 *
 * @param request the request of the primitive in which the evaluation should happen
 *
 * @author Enrico
 */
class ExpressionEvaluator<E : ExecutionContext>(
    request: Solve.Request<E>,
    index: Int?
) : AbstractEvaluator<E, Numeric>(request, index) {
    override fun unevaluable(struct: Struct): Numeric =
        throw TypeError.forArgument(request.context, request.signature, TypeError.Expected.EVALUABLE, struct, index)
}
