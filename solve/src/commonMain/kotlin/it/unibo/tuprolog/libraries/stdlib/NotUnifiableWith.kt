package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.unify.Unification.Companion.matches

/** Implementation of '\='/2 predicate */
object NotUnifiableWith : TermRelation.WithoutSideEffects<ExecutionContext>("\\=") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            (x matches y).not()
}
