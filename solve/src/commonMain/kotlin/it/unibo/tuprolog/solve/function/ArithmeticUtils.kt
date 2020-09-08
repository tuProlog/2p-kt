package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature

fun Term.evalAsExpression(context: ExecutionContext, index: Int? = null): Term =
    if (index == null) accept(ExpressionEvaluator(context))
    else try {
        accept(ExpressionEvaluator(context))
    } catch (e: InstantiationError) {
        throw InstantiationError.forArgument(context, context.procedure!!.extractSignature(), e.culprit, index)
    } catch (e: TypeError) {
        throw TypeError.forArgument(
            context,
            context.procedure!!.extractSignature(),
            e.expectedType,
            e.actualValue,
            index
        )
    }

fun Term.evalAsArithmeticExpression(context: ExecutionContext, index: Int? = null): Numeric =
    if (index == null) accept(ArithmeticEvaluator(context))
    else try {
        accept(ArithmeticEvaluator(context))
    } catch (e: InstantiationError) {
        throw InstantiationError.forArgument(context, context.procedure!!.extractSignature(), e.culprit, index)
    } catch (e: TypeError) {
        throw TypeError.forArgument(
            context,
            context.procedure!!.extractSignature(),
            e.expectedType,
            e.actualValue,
            index
        )
    }