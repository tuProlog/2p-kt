package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.EVALUABLE
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.INTEGER
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.NUMBER
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.stdlib.function.BitwiseAnd
import it.unibo.tuprolog.solve.stdlib.function.BitwiseComplement
import it.unibo.tuprolog.solve.stdlib.function.BitwiseLeftShift
import it.unibo.tuprolog.solve.stdlib.function.BitwiseOr
import it.unibo.tuprolog.solve.stdlib.function.BitwiseRightShift

/**
 * Evaluates an expression as a [Numeric] term.
 * Throws a [TypeError] in case a non-evaluable sub-term is met.
 * Throws a [TypeError] in case the evaluation produces a non-numeric term.
 *
 * @param request the request of the primitive in which the evaluation should happen
 * @param index the index of the argument being evalued in the aforementioned primitive
 */
class ArithmeticEvaluator<E : ExecutionContext>(
    request: Solve.Request<E>,
    index: Int?,
) : AbstractEvaluator<E, Numeric>(request, index) {
    constructor(request: Solve.Request<E>) : this(request, null)

    /** This method implements all the check required by the Prolog Standard for expressions to be considered valid (statically) */
    override fun Term.staticCheck() {
        when {
            this is Var ->
                throw InstantiationError.forArgument(request.context, request.signature, this, index)
            this is Atom ->
                throw TypeError.forArgument(request.context, request.signature, EVALUABLE, this, index)
            this is Struct && this.extractSignature() !in allowedArithmeticSignatures ->
                throw TypeError.forArgument(request.context, request.signature, EVALUABLE, this, index)
        }
    }

    /** This method implements all the check required by the Prolog Standard for expressions to be considered valid (dynamically) */
    override fun Term.dynamicCheck(enclosingTerm: Struct) {
        when {
            // the argument of an arithmetic functor is evaluated to a non-numeric value
            this !is Numeric ->
                throw TypeError.forArgument(request.context, request.signature, NUMBER, this)

            // the argument of a bitwise operator is evaluated to a non-integer value
            this !is Integer && enclosingTerm.extractSignature() in bitwiseStandardOperatorsSignatures ->
                throw TypeError.forArgument(request.context, request.signature, INTEGER, this)
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
                BitwiseRightShift,
            ).map { it.signature }
        }
    }
}
