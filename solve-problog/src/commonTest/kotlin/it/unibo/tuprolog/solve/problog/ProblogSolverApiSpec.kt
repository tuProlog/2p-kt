package it.unibo.tuprolog.solve.problog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

private val operators = ProblogSolverFactory.defaultBuiltins.operators

private fun solver(theory: String): Solver =
    ProblogSolverFactory.solverWithDefaultBuiltins(staticKb = theory.parseAsTheory(operators))

private val probabilistic = SolveOptions.DEFAULT.setProbabilistic(true)

/** The (immutable) Problog solver's conveniences, on `0.6::h.` */
class ProblogSolverApiSpec :
    FunSpec({
        val h = "h".parseAsStruct()

        test("solve-list/collects-every-solution") {
            val solver = solver("0.6::h.")
            assertEquals(listOf(h), solver.solveList(h).map { it.query })
            assertEquals(listOf(h), solver.solveList(h, 1.seconds.inWholeMilliseconds).map { it.query })
            assertEquals(listOf(0.6), solver.solveList(h, probabilistic).map { it.probability })
        }

        test("solve-once/stops-at-the-first-solution") {
            val solver = solver("0.6::h.")
            assertIs<Solution.Yes>(solver.solveOnce(h))
            assertIs<Solution.Yes>(solver.solveOnce(h, 1.seconds.inWholeMilliseconds))
            assertEquals(0.6, solver.solveOnce(h, probabilistic).probability)
        }

        test("solve/an-impossible-goal-has-probability-zero") {
            assertEquals(0.0, solver("0.6::h.").solveOnce("k".parseAsStruct(), probabilistic).probability)
        }

        test("copy/maps-the-theories-it-is-given") {
            val copy = solver("").copy(staticKb = "0.6::h.".parseAsTheory(operators))
            assertEquals(0.6, copy.solveOnce(h, probabilistic).probability)
        }

        test("clone/keeps-the-knowledge-base") {
            assertEquals(0.6, solver("0.6::h.").clone().solveOnce(h, probabilistic).probability)
        }
    })
