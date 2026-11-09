package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Conformance tests for how a resolved [Solution]'s substitution treats variables that do not directly appear in the
 * original query ("uninteresting" variables, introduced only by intermediate goals/clause bodies), and showing that the
 * deprecated `track_variables` flag changes nothing: a binding still needed by a later goal of a clause is kept
 * whatever the flag (#1078).
 */
fun substitutionsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Solves `p(A, B, C)` against [TestingClauseTheories.callsWithVariablesAndInspectorTheory]'s theory, with the
    // flag unset, `on` and `off`: the inspector observes the same variables at every call site each time, including
    // the intermediate `D` (bound only inside the theory's own rule bodies) at the fourth one.
    scenario("substitutions/interesting-variables-are-tracked-whatever-track-variables") {
        trackVariablesSettings.forEach { flags ->
            inspectedVariables(flags) shouldBe
                listOf(setOf("A", "B", "C"), setOf("D"), setOf("E"), setOf("A", "B", "C", "D", "E"))
        }
    }

    // Solves every `a(X)` against [TestingClauseTheories.callsWithVariablesTheory] with the flag unset, `on` and
    // `off`: the solutions are the same each time.
    scenario("substitutions/track-variables-does-not-change-solutions") {
        logicProgramming {
            val solutions =
                trackVariablesSettings.map { flags ->
                    solverFactory
                        .solverOf(staticKb = TestingClauseTheories.callsWithVariablesTheory, flags = flags)
                        .solve("a"(X))
                        .toList()
                }
            solutions.distinct().size shouldBe 1
        }
    }

    // Programs that still set the deprecated flag keep working.
    case("substitutions/track-variables-can-still-be-set") {
        "set_prolog_flag"("track_variables", "on") and "current_prolog_flag"("track_variables", "X") yields
            { listOf(it.yes("X" to "on")) }
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
            solution.shouldBeInstanceOf<Solution.Yes>()
            solution.substitution.size shouldBe 1
            solution.substitution[X] shouldBe intOf(1)
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

// unset, `on` and `off`
private val trackVariablesSettings =
    listOf(
        FlagStore.DEFAULT,
        FlagStore.DEFAULT + ("track_variables" to Atom.of("on")),
        FlagStore.DEFAULT + ("track_variables" to Atom.of("off")),
    )

private fun ContractScope.inspectedVariables(flags: FlagStore): List<Set<String>> {
    val inspections = mutableListOf<Set<String>>()
    logicProgramming {
        val inspect = Inspect(inspections)
        val solver =
            solverFactory.solverOf(
                staticKb = TestingClauseTheories.callsWithVariablesAndInspectorTheory("p", inspect.functor),
                libraries = runtimeOf("default", inspect),
                flags = flags,
            )
        solver.solveOnce("p"(A, B, C))
    }
    return inspections
}
