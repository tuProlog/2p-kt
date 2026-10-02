package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object CopyTerm : BinaryRelation.Functional<ExecutionContext>("copy_term") {
    override val help: String =
        """
        `copy_term(+Term, -Copy)`
        
        Unifies `Copy` with a fresh copy of `Term`, preserving term structure while replacing variables with fresh variables.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution = mgu(first.freshCopy(), second)
}
