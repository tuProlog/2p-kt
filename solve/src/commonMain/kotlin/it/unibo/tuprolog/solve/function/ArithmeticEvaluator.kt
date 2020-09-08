package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.Companion.EVALUABLE
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.Companion.INTEGER
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.Companion.NUMBER
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.stdlib.function.*

/**
 * Prolog Arithmetic Expression evaluator implementation
 *
 * @author Enrico
 */
class ArithmeticEvaluator<E : ExecutionContext>(request: Solve.Request<E>, index: Int?) :
    AbstractEvaluator<E, Numeric>(request, index) {

    constructor(request: Solve.Request<E>) : this(request, null)

    // this override is needed to treat "/" functor as an arithmetic one, among the others
    override fun visit(term: Indicator): Numeric = super.visitStruct(term)

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
                BitwiseRightShift
            ).map { it.signature }
        }
    }
}
