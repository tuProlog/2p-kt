package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of `'\=='/2` predicate */
object TermNotIdentical : TermRelation.WithoutSideEffects<ExecutionContext>("\\==") {
    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
        x != y
}
