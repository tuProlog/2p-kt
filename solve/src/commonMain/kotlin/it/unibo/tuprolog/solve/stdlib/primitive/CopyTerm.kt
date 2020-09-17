package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object CopyTerm : BinaryRelation.Functional<ExecutionContext>("copy_term") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        first.freshCopy() mguWith second
}
