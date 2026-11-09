package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.search.GoalSelectionPolicy
import it.unibo.tuprolog.solve.search.RuleSelectionPolicy

/**
 * A [RuleSelectionPolicy] or [GoalSelectionPolicy] in the [SolveOptions] changes the resolution order (#1010, section
 * 7.4).
 */
fun selectionPolicyContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("selection/reversed-clause-order") {
        val reversed = RuleSelectionPolicy { _, candidates, _ -> candidates.toList().asReversed().asSequence() }
        solverFactory
            .traced(
                "q(X)",
                "q(1).",
                "q(2).",
                "q(3).",
                options = SolveOptions.allLazily().setRuleSelection(reversed),
            ).answers shouldBe listOf("yes: X = 3", "yes: X = 2", "yes: X = 1")
    }

    scenario("selection/rightmost-goal-first") {
        val rightmost = GoalSelectionPolicy { goals, _ -> goals.lastIndex }
        val run =
            solverFactory.traced(
                "p(X), r(X, Y)",
                "p(1).",
                "r(1, a).",
                options = SolveOptions.allLazily().setGoalSelection(rightmost),
            )
        // the same answer, through another selection order
        run.answers shouldBe listOf("yes: X = 1, Y = a")
        run.trace shouldBe listOf("call r(_, _)", "exit r(1, a)", "call p(1)", "exit p(1)")
    }
}
