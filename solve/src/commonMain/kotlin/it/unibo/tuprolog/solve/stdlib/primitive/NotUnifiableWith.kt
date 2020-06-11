package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TermRelation
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** Implementation of '\='/2 predicate */
object NotUnifiableWith : TermRelation.WithoutSideEffects<ExecutionContext>("\\=") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
        (x matches y).not()
}
