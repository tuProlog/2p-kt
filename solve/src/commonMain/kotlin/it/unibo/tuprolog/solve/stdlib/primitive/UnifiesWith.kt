package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/** Implementation of '='/2 predicate */
object UnifiesWith : BinaryRelation.Functional<ExecutionContext>("=") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        first mguWith second
}
