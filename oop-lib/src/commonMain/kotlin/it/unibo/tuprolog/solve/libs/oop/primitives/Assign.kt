package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object Assign : TernaryRelation.Predicative<ExecutionContext>("assign") {

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
        third: Term
    ): Boolean {
        ensuringArgumentIsRef(0)
        ensuringArgumentIsAtom(1)
        return (first as Ref).assign((second as Atom).value, third)
    }

}