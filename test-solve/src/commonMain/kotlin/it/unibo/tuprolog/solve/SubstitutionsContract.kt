package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Conformance tests for how a resolved [Solution]'s substitution treats variables that do not directly appear in the original query ("uninteresting" variables, introduced only by intermediate goals/clause bodies), and for the [it.unibo.tuprolog.solve.flags.TrackVariables] flag that controls whether they are kept visible to a custom primitive during resolution. */
fun substitutionsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Runs [interestingVariablesAreProperlyTracked]'s scenario with
    // [it.unibo.tuprolog.solve.flags.TrackVariables] `off`, and checks that the query's own variables (`A`, `B`,
    // `C`) are still observed by the inspector at every call site regardless — only the *intermediate* variable
    // `D` (introduced by `callsWithVariablesAndInspectorTheory`'s own rule bodies) is affected by the flag.
    scenario("substitutions/interesting-variables-are-not-obliterated") {
        testVariablesTracking(false)
    }

    // Solves `p(A, B, C)` against [TestingClauseTheories.callsWithVariablesAndInspectorTheory]'s theory with
    // [it.unibo.tuprolog.solve.flags.TrackVariables] `on`; the intermediate variable `D` (bound only inside the
    // theory's own rule bodies, not directly by the query) is observed by the inspector at its fourth call site,
    // alongside the query's own `A`/`B`/`C`/`E` — it is not discarded even though it is not one of the query's
    // variables.
    scenario("substitutions/interesting-variables-are-properly-tracked") {
        testVariablesTracking(true)
    }

    // Solves `a(X)` against [TestingClauseTheories.callsWithVariablesTheory] (`a(A) :- b(A), d(Z).` and similar
    // rules introducing intermediate variables `Z`/`W`); succeeds with `X` bound to `1`, and the resulting
    // substitution contains only `X` — the intermediate variables introduced along the way are obliterated from
    // the final substitution once they are no longer needed.
    scenario("substitutions/uninteresting-variables-are-obliterated") {
        logicProgramming {
            val solver = solverFactory.solverOf(staticKb = TestingClauseTheories.callsWithVariablesTheory)
            val query = "a"(X)
            val solution = solver.solveOnce(query)
            assertIs<Solution.Yes>(solution)
            assertEquals(1, solution.substitution.size)
            assertEquals(intOf(1), solution.substitution[X])
        }
    }
}

private class Inspect(
    private val inspections: MutableList<Set<String>>,
) : UnaryPredicate.Predicative<ExecutionContext>("inspect") {
    override fun Solve.Request<ExecutionContext>.compute(first: Term): Boolean {
        inspections +=
            first.variables
                .distinct()
                .map { context.substitution.getOriginal(it) }
                .filterNotNull()
                .map { it.name }
                .toSet()
        return true
    }
}

private fun ContractScope.testVariablesTracking(tracking: Boolean) {
    val inspections = mutableListOf<Set<String>>()
    logicProgramming {
        val inspect = Inspect(inspections)
        val solver =
            solverFactory.solverOf(
                staticKb = TestingClauseTheories.callsWithVariablesAndInspectorTheory("p", inspect.functor),
                libraries = runtimeOf("default", inspect),
                flags = FlagStore.EMPTY + TrackVariables { if (tracking) ON else OFF },
            )
        val query = "p"(A, B, C)
        solver.solveOnce(query)
    }
    assertEquals(setOf("A", "B", "C"), inspections[0])
    assertEquals(setOf("D"), inspections[1])
    assertEquals(setOf("E"), inspections[2])
    assertEquals(setOf("A", "B", "C", "E") + if (tracking) setOf("D") else emptySet(), inspections[3])
}
