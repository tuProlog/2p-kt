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

/**
 * Evaluates this [Term] as an expression against [request]'s context-loaded functions, reducing evaluable
 * sub-terms in place via [ExpressionEvaluator]; a non-evaluable sub-term raises a
 * [it.unibo.tuprolog.solve.exception.error.TypeError]. [index] is used only to enrich that error, identifying which
 * argument of [request] this term came from.
 */
fun Term.evalAsExpression(
    request: Solve.Request<*>,
    index: Int? = null,
): Term = eval(request, index, ::ExpressionEvaluator)

/**
 * Evaluates this [Term] as a full arithmetic expression (as `is/2` does), via [ArithmeticEvaluator], producing a
 * [Numeric] result or raising an error if any sub-term is not evaluable, or does not evaluate to a number. [index]
 * is used only to enrich thrown errors, identifying which argument of [request] this term came from.
 */
fun Term.evalAsArithmeticExpression(
    request: Solve.Request<*>,
    index: Int? = null,
): Numeric = eval(request, index, ::ArithmeticEvaluator)
