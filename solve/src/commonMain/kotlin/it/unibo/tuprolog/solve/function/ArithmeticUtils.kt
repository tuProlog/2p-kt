package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

private fun <E : ExecutionContext, T : Term> Term.eval(
    request: Solve.Request<E>,
    index: Int? = null,
    evaluator: (Solve.Request<E>, Int?) -> AbstractEvaluator<E, T>,
): T = accept(evaluator(request, index))

private fun <E : ExecutionContext, T : Term> Term.eval(
    request: Solve.Request<E>,
    evaluator: (Solve.Request<E>) -> AbstractEvaluator<E, T>,
): T = accept(evaluator(request))

fun Term.evalAsExpression(
    request: Solve.Request<*>,
    index: Int? = null,
): Term = eval(request, index, ::ExpressionEvaluator)

fun Term.evalAsArithmeticExpression(
    request: Solve.Request<*>,
    index: Int? = null,
): Numeric = eval(request, index, ::ArithmeticEvaluator)
