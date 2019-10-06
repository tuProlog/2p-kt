package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.mguWith

object UnifiesWith : TermRelation.WithSideEffects("=") {
    override fun relationWithSideEffects(x: Term, y: Term): Substitution =
            x mguWith y
}