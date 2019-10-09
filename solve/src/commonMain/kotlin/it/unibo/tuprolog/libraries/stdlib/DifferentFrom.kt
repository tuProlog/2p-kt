package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of `'\=='/2` predicate */
object DifferentFrom : TermRelation.WithoutSideEffects<ExecutionContext>("\\==") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
            x != y
}
