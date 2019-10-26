package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.stdlib.CommonBuiltins
import it.unibo.tuprolog.libraries.stdlib.function.*
import it.unibo.tuprolog.primitive.toSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Prolog Arithmetic Expression evaluator implementation
 *
 * @author Enrico
 */
open class ArithmeticEvaluator(context: ExecutionContext) : ExpressionEvaluator(context) {

    // this override is needed to treat "/" functor as an arithmetic one, among the others
    override fun visit(term: Indicator): Term = super.visitStruct(term)

    /** This method implements all the check required by the Prolog Standard for expressions to be considered valid (statically) */
    override fun Term.staticCheck(context: ExecutionContext) {
        when {
            this is Var -> throw InstantiationError(
                    "Variable sub-expressions are not allowed: `$this`",
                    context = context,
                    extraData = this
            )
            this is Atom -> throw TypeError(
                    "Atom sub-expression are not allowed: `$this`",
                    context = context,
                    expectedType = TypeError.Expected.EVALUABLE,
                    actualValue = this
            )
            this is Struct && this.toSignature() !in allowedArithmeticSignatures -> throw TypeError(
                    "The struct `$this` is not part of allowed arithmetic functions",
                    context = context,
                    expectedType = TypeError.Expected.EVALUABLE,
                    actualValue = this
            )
        }
    }

    /** This method implements all the check required by the Prolog Standard for expressions to be considered valid (dynamically) */
    override fun Term.dynamicCheck(context: ExecutionContext) {
        when {
            // the argument of an arithmetic functor is evaluated to a non-numeric value
            this is Struct -> this.args.indexOfFirst { it !is Numeric }.takeIf { it != -1 }?.let { wrongArgIndex ->
                throw TypeError(
                        context,
                        this.toSignature(),
                        TypeError.Expected.NUMBER,
                        this[wrongArgIndex],
                        wrongArgIndex
                )
            }

            // the argument of a bitwise operator is evaluated to a non-integer value
            this is Struct && this.toSignature() in bitwiseStandardOperatorsSignatures ->
                this.args.indexOfFirst { it !is Integer }.takeIf { it != -1 }?.let { wrongArgIndex ->
                    throw TypeError(
                            context,
                            this.toSignature(),
                            TypeError.Expected.INTEGER,
                            this[wrongArgIndex],
                            wrongArgIndex
                    )
                }
        }
    }

    companion object {

        /** All allowed arithmetic signatures in Prolog expressions */
        val allowedArithmeticSignatures by lazy {
            CommonBuiltins.functions.map { (signature, _) -> signature }
        }

        /** Prolog standard bitwise operators */
        internal val bitwiseStandardOperatorsSignatures by lazy {
            listOf(
                    BitwiseAnd,
                    BitwiseComplement,
                    BitwiseLeftShift,
                    BitwiseOr,
                    BitwiseRightShift
            ).map { it.signature }
        }
    }
}
