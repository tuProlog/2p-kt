package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import it.unibo.tuprolog.core.List as LogicList

private const val TIMEOUT = 10_000L

/** `member(X, [1, 2, 3])`, whose solutions are the reference every convenience call must reproduce. */
private val goal = Struct.of("member", Var.of("X"), LogicList.of(Integer.of(1), Integer.of(2), Integer.of(3)))

private fun newSolver(): Solver = Solver.prolog.solverWithDefaultBuiltins()

/** A convenience call, and how many of the reference solutions it must give back. */
private class CallCase(
    val id: String,
    val expectedCount: Int,
    val call: Solver.(Struct) -> List<Solution>,
)

private val calls =
    listOf(
        CallCase("solve/timeout", 4) { solve(it, TIMEOUT).toList() },
        CallCase("solve-list", 4) { solveList(it) },
        CallCase("solve-list/timeout", 4) { solveList(it, TIMEOUT) },
        CallCase("solve-list/options", 2) { solveList(it, SolveOptions.someEagerly(2)) },
        CallCase("solve-once", 1) { listOf(solveOnce(it)) },
        CallCase("solve-once/timeout", 1) { listOf(solveOnce(it, TIMEOUT)) },
        CallCase("solve-once/options", 1) { listOf(solveOnce(it, SolveOptions.allEagerly())) },
    )

class SolverConvenienceSpec :
    FunSpec({
        withTests(nameFn = { it.id }, calls) { case ->
            // three yes solutions, then a final no
            val reference = newSolver().solve(goal).toList()
            assertSolutionEquals(reference.take(case.expectedCount), newSolver().(case.call)(goal))
        }

        test("clone/copies-the-knowledge-base-into-an-independent-solver") {
            val solver =
                Solver.prolog.mutableSolverWithDefaultBuiltins(
                    staticKb = Theory.of(Fact.of(Struct.of("f", Integer.of(1)))),
                )
            val clone = solver.clone()
            assertNotSame(solver, clone)
            assertEquals(solver.staticKb, clone.staticKb)
            solver.loadStaticClauses(Fact.of(Struct.of("f", Integer.of(2))))
            assertEquals(1, clone.solveList(Struct.of("f", Var.of("X"))).count { it.isYes })
        }

        test("copy/replaces-only-what-it-is-given") {
            val theory = Theory.of(Fact.of(Struct.of("f", Integer.of(1))))
            val solver = Solver.prolog.solverWithDefaultBuiltins()
            val copy = solver.copy(staticKb = theory)
            assertEquals(theory, copy.staticKb)
            assertEquals(solver.libraries, copy.libraries)
            assertEquals(
                theory,
                Solver.prolog
                    .solverWithDefaultBuiltins(staticKb = theory)
                    .clone()
                    .staticKb,
            )
        }
    })
