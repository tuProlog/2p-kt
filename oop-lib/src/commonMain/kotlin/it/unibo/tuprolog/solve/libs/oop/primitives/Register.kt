package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Register : BinaryRelation.NonBacktrackable<ExecutionContext>("register") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsRef(0)
        ensuringArgumentIsStruct(1)
        if (!second.isGround) {
            throw InstantiationError.forArgument(context, signature, second.variables.first(), 0)
        }
        return replySuccess {
            addStaticClauses(Alias.of(second as Struct, first as Ref).wrappedImplementation)
        }
    }
}
