package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches

object DifferentFrom : TermRelation.WithoutSideEffects("\\==") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            x != y
}