package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object Assign : TernaryRelation.Predicative<ExecutionContext>("assign") {

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
        third: Term
    ): Boolean {
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsAtom(1)

        val ref = when (first) {
            is Ref -> first
            else -> findRefFromAlias(first as Atom)
        } ?: throw TypeError.forArgument(context, signature, TypeError.Expected.REFERENCE, first, 0)

        return ref.assign((second as Atom).value, third)
    }
}
