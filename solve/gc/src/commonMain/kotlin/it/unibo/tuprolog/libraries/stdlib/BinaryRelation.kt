package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class BinaryRelation(operator: String) : PrimitiveWrapper<ExecutionContext>(operator, 2) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
            sequenceOf(
                    request.ensuringAllArgumentsAreInstantiated()
                            .computeSingleResponse()
            )

    protected abstract fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response

    protected open fun relationWithoutSideEffects(x: Term, y: Term): Boolean = throw NotImplementedError()

    protected open fun relationWithSideEffects(x: Term, y: Term): Substitution = throw NotImplementedError()
}