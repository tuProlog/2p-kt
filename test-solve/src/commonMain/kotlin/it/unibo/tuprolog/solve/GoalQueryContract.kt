package it.unibo.tuprolog.solve

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.Theory
import kotlin.time.Duration.Companion.seconds

private fun SolverFactory.solverOfFacts() =
    solverWithDefaultBuiltins(
        staticKb =
            Theory.of(
                Fact.of(Struct.of("p", Integer.of(1))),
                Fact.of(Struct.of("q", Integer.of(1))),
                Fact.of(Struct.of("q", Integer.of(2))),
            ),
    )

/**
 * Solvers solve the bodies of [Goal]s as they solve calls: solutions refer to the goal of what was solved, equal to the
 * given one.
 */
fun goalQueryContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("goal-query/solutions-refer-to-the-goal") {
        val x = Var.of("X")
        val goal = Goal.of(Struct.of("p", x))
        val solutions = solverFactory.solverOfFacts().solve(goal, mediumBudget).toList()
        solutions shouldBe listOf(Solution.yes(goal, Substitution.of(x, Integer.of(1))))
        solutions.single().query shouldBe goal
        solutions.single().solvedQuery shouldBe Goal.of(Struct.of("p", Integer.of(1)))
    }

    scenario("goal-query/conjunctive-goal") {
        val x = Var.of("X")
        val goal = Goal.of(Tuple.of(Struct.of("q", x), Struct.of("p", x)))
        val solutions = solverFactory.solverOfFacts().solve(goal, mediumBudget).toList()
        solutions.filterIsInstance<Solution.Yes>() shouldBe
            listOf(Solution.yes(goal, Substitution.of(x, Integer.of(1))))
    }

    scenario("goal-query/failure-and-halt-refer-to-the-goal") {
        val solver = solverFactory.solverOfFacts()
        val failing = Goal.of(Struct.of("p", Integer.of(2)))
        solver.solveOnce(failing, mediumBudget).query shouldBe failing
        val throwing = Goal.of(Struct.of("throw", Atom.of("e")))
        val halt = solver.solveOnce(throwing, mediumBudget)
        halt.shouldBeInstanceOf<Solution.Halt>()
        halt.query shouldBe throwing
    }

    scenario("goal-query/calls-are-solved-as-their-goals") {
        val x = Var.of("X")
        val call = Struct.of("q", x)
        val solver = solverFactory.solverOfFacts()
        val ofCall = solver.solve(call, mediumBudget).toList()
        ofCall.map { it.query } shouldBe listOf(Goal.of(call), Goal.of(call))
        // in any order, as concurrent solvers may find them
        ofCall shouldContainExactlyInAnyOrder solver.solve(Goal.of(call), mediumBudget).toList()
    }

    scenario("goal-query/every-overload-solves-the-body") {
        val x = Var.of("X")
        val goal = Goal.of(Struct.of("p", x))
        val expected = Solution.yes(goal, Substitution.of(x, Integer.of(1)))
        val solver = solverFactory.solverOfFacts()
        solver.solve(goal).toList() shouldBe listOf(expected)
        solver.solve(goal, 10.seconds).toList() shouldBe listOf(expected)
        solver.solveOnce(goal) shouldBe expected
        solver.solveOnce(goal, 10.seconds) shouldBe expected
        solver.solveOnce(goal, mediumBudget) shouldBe expected
    }

    scenario("goal-query/ground-goal") {
        val goal = Goal.of(Struct.of("q", Integer.of(2)))
        solverFactory.solverOfFacts().solveOnce(goal, mediumBudget) shouldBe Solution.yes(goal)
    }
}
