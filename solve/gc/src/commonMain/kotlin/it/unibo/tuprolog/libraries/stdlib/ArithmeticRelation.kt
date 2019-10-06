package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solve

abstract class ArithmeticRelation(operator: String) : BinaryRelation(operator) {
    override fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response> =
            sequenceOf(
                    ensuringAllArgumentsAreInstantiated(request) {
                        ensuringAllArgumentsAreNumeric(it) {
                            computeSingleResponse(it)
                        }
                    }
            )

    override fun computeSingleResponse(request: Solve.Request): Solve.Response =
            request.toResponse(relationWithoutSideEffects(request.arguments[0], request.arguments[1]))

    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            arithmeticRelation(x as Numeric, y as Numeric)

    protected abstract fun arithmeticRelation(x: Numeric, y: Numeric): Boolean
}