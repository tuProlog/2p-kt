package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/**
 * Base class to implement arithmetic functions
 *
 * @author Enrico
 */
abstract class MathFunction : FunctionWrapper<ExecutionContext> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** Utility function to throw int overflow math error */
    protected fun throwIntOverflowError(context: ExecutionContext): Nothing =
        throw EvaluationError(context = context, errorType = EvaluationError.Type.INT_OVERFLOW)

    /** Utility function to throw float overflow math error */
    protected fun throwFloatOverflowError(context: ExecutionContext): Nothing =
        throw EvaluationError(context = context, errorType = EvaluationError.Type.FLOAT_OVERFLOW)

    /** Utility function to throw underflow math error */
    protected fun throwUnderflowError(context: ExecutionContext): Nothing =
        throw EvaluationError(context = context, errorType = EvaluationError.Type.UNDERFLOW)

    /** Utility function to throw zero division math error */
    protected fun throwZeroDivisorError(context: ExecutionContext): Nothing =
        throw EvaluationError(context = context, errorType = EvaluationError.Type.ZERO_DIVISOR)

    /** Utility function to throw undefined math error */
    protected fun throwUndefinedError(context: ExecutionContext): Nothing =
        throw EvaluationError(context = context, errorType = EvaluationError.Type.UNDEFINED)

    /** Utility function to throw a TypeError for operators requiring only integers as parameters */
    protected fun throwTypeErrorBecauseOnlyIntegersAccepted(
        opName: String,
        actualValue: Term,
        context: ExecutionContext
    ): Nothing = throw TypeError(
        "Operator `$opName` accepts only integers!",
        context = context,
        expectedType = TypeError.Expected.INTEGER,
        culprit = actualValue
    )
}
