package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

object Repeat : PredicateWithoutArguments.WithoutSideEffects<ExecutionContext>("repeat") {
    override val help: String =
        """
        `repeat`
        
        Always succeeds and leaves a choice point that succeeds again indefinitely on backtracking.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(): Sequence<Substitution> =
        generateSequence(Substitution.empty()) { Substitution.empty() }
}
