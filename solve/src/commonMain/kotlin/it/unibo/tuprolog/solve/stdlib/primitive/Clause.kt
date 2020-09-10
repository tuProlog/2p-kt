package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.ACCESS
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.PRIVATE_PROCEDURE
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.utils.buffered

object Clause : BinaryRelation.WithoutSideEffects<ExecutionContext>("clause") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsStruct(0)
        if (second !is Var) {
            ensuringArgumentIsCallable(1)
        }
        val head = first as Struct
        val headSignature = head.extractSignature()
        if (context.libraries.hasProtected(headSignature)) {
            throw PermissionError.of(context, signature, ACCESS, PRIVATE_PROCEDURE, headSignature.toIndicator())
        }
        return (context.staticKb[head] + context.dynamicKb[head].buffered()).map {
            (it.head mguWith head) + (it.body mguWith second)
        }.filter {
            it.isSuccess
        }
    }

}