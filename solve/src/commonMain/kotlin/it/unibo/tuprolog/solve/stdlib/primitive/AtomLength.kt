package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object AtomLength : BinaryRelation.Functional<ExecutionContext>("atom_length") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        when {
            first is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }
            second is Var -> {
                ensuringArgumentIsAtom(0)
                val atomLength = (first as Atom).toString().lastIndex.inc()
                Substitution.of(second, atomLength.toTerm())
            }
            else -> {
                ensuringArgumentIsNonNegativeInteger(1)
                ensuringArgumentIsAtom(0)
                val atomLength = (first as Atom).toString().lastIndex.inc().toTerm()
                atomLength mguWith second
            }
        }
}