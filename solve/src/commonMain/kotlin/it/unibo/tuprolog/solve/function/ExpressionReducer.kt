package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Reduces a [Term] as an expression by evaluating all the evaluable sub-terms it contains
 * w.r.t. the loaded functions provided through [request].
 * If the input expression is non-evaluable, it remains unaffected and no error is thrown
 *
 * @param request the request of the primitive in which the evaluation should happen
 * @param index the index of the argument being evalued in the aforementioned primitive
 */
class ExpressionReducer<E : ExecutionContext>(
    request: Solve.Request<E>,
    index: Int? = null,
) : AbstractEvaluator<E, Term>(request, index) {
    override fun unevaluable(struct: Struct): Term =
        if (struct.arity > 0) {
            struct.setArgs(struct.argsSequence.map { it.accept(this).apply { dynamicCheck(struct) } })
        } else {
            struct
        }
}
