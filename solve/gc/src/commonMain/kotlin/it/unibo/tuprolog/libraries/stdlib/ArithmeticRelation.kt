package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class ArithmeticRelation(operator: String) : BinaryRelation(operator) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
            sequenceOf(
                    request.ensuringAllArgumentsAreInstantiated()
                            .ensuringAllArgumentsAreNumeric()
                            .computeSingleResponse()
            )

    override fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response =
            replyWith(relationWithoutSideEffects(arguments[0], arguments[1]))

    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            arithmeticRelation(x as Numeric, y as Numeric)

    protected abstract fun arithmeticRelation(x: Numeric, y: Numeric): Boolean
}
