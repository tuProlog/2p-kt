package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.unify.Unification.Companion.mguWith

/** Implementation of '='/2 predicate */
object UnifiesWith : TermRelation.WithSideEffects<ExecutionContext>("=") {
    override fun relationWithSideEffects(x: Term, y: Term): Substitution =
        x mguWith y
}
