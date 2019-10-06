package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches

object NotUnifiableWith : TermRelation.WithoutSideEffects("\\=") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            (x matches y).not()
}