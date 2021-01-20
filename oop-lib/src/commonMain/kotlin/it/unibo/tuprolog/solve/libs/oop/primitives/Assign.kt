package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.matches

object Assign : TernaryRelation.Predicative<ExecutionContext>("assign") {

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
        third: Term
    ): Boolean {
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsAtom(1)

        return catchingOopExceptions {
            val ref = when (first) {
                is Ref -> first
                else -> findRefFromAlias(first as Atom)
            }

            val value = if (third matches DEALIASING_TEMPLATE) {
                findRefFromAlias(third as Struct)
            } else {
                third
            }

            ref.assign(termToObjectConverter, (second as Atom).value, value)
        }
    }
}
