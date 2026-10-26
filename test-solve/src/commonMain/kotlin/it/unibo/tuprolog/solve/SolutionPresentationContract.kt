package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Conformance tests for how a [Solution]'s substitution presents variables that only occur nested inside another variable's binding (i.e. */
fun solutionPresentationContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests the query
    // ```prolog
    // ?- append(A, B).
    // ```
    // against the fact `append(seq(X), X).`; succeeds binding `A` to `seq(X)` (a fresh renaming of the fact's own
    // `X`) and `B` to that same fresh variable, and checks that both `A` and `B` are present in the returned
    // substitution's keys — including `B`, whose "value" is itself a dangling variable (named `X`) rather than a
    // ground term.
    scenario("solution-presentation/solution-with-dandling-vars") {
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "append"("seq"(X), X) },
                )
            val solver = solverFactory.solverOf(staticKb = theory)
            val query = "append"(A, B)
            val sol = solver.solveOnce(query)
            assertTrue { sol is Solution.Yes }
            assertTrue { setOf(A, B).all { it in sol.substitution.keys } }
            assertTrue { sol.substitution[A]!! matches "seq"(`_`) }
            assertEquals("X", (sol.substitution[B] as? Var)?.name)
        }
    }
}
