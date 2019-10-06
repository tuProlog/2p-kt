package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class BinaryRelation(operator: String) : PrimitiveWrapper(operator, 2, false) {
    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
            sequenceOf(
                    ensuringAllArgumentsAreInstantiated(request) {
                        computeSingleResponse(it)
                    }
            )

    protected abstract fun computeSingleResponse(request: Solve.Request<ExecutionContext>): Solve.Response

    protected open fun relationWithoutSideEffects(x: Term, y: Term): Boolean = TODO()

    protected open fun relationWithSideEffects(x: Term, y: Term): Substitution = TODO()
}