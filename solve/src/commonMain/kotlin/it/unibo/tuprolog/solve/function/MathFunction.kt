package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.RealPrecisionDigits
import it.unibo.tuprolog.solve.flags.RealPrecisionMode
import it.unibo.tuprolog.solve.flags.RealRoundingMode
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import org.gciatto.kt.math.big.exponent

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

    /**
     * Computes a [Real] via [operation], as the [RealPrecisionMode], [RealPrecisionDigits] and [RealRoundingMode]
     * flags of [context] set: [operation] receives the [MathContext] to compute with, [MathContext.UNLIMITED] when
     * an exact result is sought. An exact attempt that fails (with an [ArithmeticException], or an
     * [UnsupportedOperationException] if [operation] doesn't support unlimited precision) falls back to a rounded
     * one, or raises the `undefined` evaluation error, as [RealPrecisionMode] sets. A failing rounded attempt raises the
     * `float_overflow` or `underflow` evaluation error, if the result's exponent doesn't fit an [Int], `undefined` otherwise.
     */
    protected fun computeReal(
        context: ExecutionContext,
        operation: (MathContext) -> BigDecimal,
    ): Real {
        val mode = context.flags[RealPrecisionMode] ?: RealPrecisionMode.defaultValue
        val exact = if (mode == RealPrecisionMode.LIMITED) null else exactly(operation)
        return Real.of(
            when {
                exact != null -> exact
                mode == RealPrecisionMode.UNLIMITED -> throwUndefinedError(context)
                else ->
                    try {
                        operation(context.flags.realMathContext)
                    } catch (e: ArithmeticException) {
                        throwEvaluationError(e, context)
                    }
            },
        )
    }

    /**
     * Computes [operation] with as many significant digits as [mathContext] sets, even when the result is much
     * smaller than 1: big-math's series are accurate to [mathContext]'s digits relative to 1, not to the result, so a
     * result near a zero of the function (e.g. `cos(pi/2)`) loses a digit per order of magnitude. Such a result is
     * recomputed with as many more digits, then rounded. Unlimited precision is passed through as is.
     */
    protected fun relativelyPrecise(
        mathContext: MathContext,
        operation: (MathContext) -> BigDecimal,
    ): BigDecimal {
        val first = operation(mathContext)
        val lostDigits = -first.exponent() + GUARD_DIGITS
        return if (mathContext.precision == 0 || first.signum == 0 || lostDigits <= GUARD_DIGITS) {
            first
        } else {
            operation(MathContext(mathContext.precision + lostDigits, mathContext.roundingMode)).round(mathContext)
        }
    }

    /** kt-math reports a result whose exponent doesn't fit an [Int] by an [ArithmeticException] with this message. */
    private fun throwEvaluationError(
        e: ArithmeticException,
        context: ExecutionContext,
    ): Nothing =
        when (e.message) {
            "Overflow" -> throwFloatOverflowError(context)
            "Underflow" -> throwUnderflowError(context)
            else -> throwUndefinedError(context)
        }

    private fun exactly(operation: (MathContext) -> BigDecimal): BigDecimal? =
        try {
            operation(MathContext.UNLIMITED)
        } catch (_: ArithmeticException) {
            null
        } catch (_: UnsupportedOperationException) {
            null
        }

    /** Utility function to throw a TypeError for operators requiring only integers as parameters */
    protected fun throwTypeErrorBecauseOnlyIntegersAccepted(
        opName: String,
        actualValue: Term,
        context: ExecutionContext,
    ): Nothing =
        throw TypeError(
            "Operator `$opName` accepts only integers!",
            context = context,
            expectedType = TypeError.Expected.INTEGER,
            culprit = actualValue,
        )
}

/**
 * The [MathContext] reals are rounded with, whenever [RealPrecisionMode] calls for rounding: [RealPrecisionDigits]
 * digits, rounded as [RealRoundingMode] sets. Unset or inadmissible flags take their default values, so this is
 * [MathContext.DECIMAL64] for [FlagStore.DEFAULT].
 */
val FlagStore.realMathContext: MathContext
    get() {
        val digits =
            this[RealPrecisionDigits]?.takeIf { RealPrecisionDigits.isAdmissibleValue(it) }
                ?: RealPrecisionDigits.defaultValue
        val roundingMode =
            this[RealRoundingMode]?.let { RealRoundingMode.toRoundingMode(it) }
                ?: RoundingMode.HALF_EVEN
        return MathContext(digits.castToInteger().intValue.toInt(), roundingMode)
    }

private const val GUARD_DIGITS = 2
