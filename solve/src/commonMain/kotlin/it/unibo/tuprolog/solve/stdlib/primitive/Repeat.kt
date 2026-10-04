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

        **Examples**

        ```prolog
        ?- assertz(c(0)).
        yes.

        % repeat until the counter reaches 3, then cut the choice point
        ?- repeat, retract(c(N)), M is N + 1, assertz(c(M)), M >= 3, !.
        M = 3.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(): Sequence<Substitution> =
        generateSequence(Substitution.empty()) { Substitution.empty() }
}
