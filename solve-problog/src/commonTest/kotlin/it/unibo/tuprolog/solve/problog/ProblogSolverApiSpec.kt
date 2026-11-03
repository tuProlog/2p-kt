package it.unibo.tuprolog.solve.problog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.parsing.parseAsTheory
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
            (solver.solveList(h).map { it.query }) shouldBe listOf(h)
            (solver.solveList(h, 1.seconds).map { it.query }) shouldBe listOf(h)
            (solver.solveList(h, probabilistic).map { it.probability }) shouldBe listOf(0.6)
        }

        test("solve-once/stops-at-the-first-solution") {
            val solver = solver("0.6::h.")
            solver.solveOnce(h).shouldBeInstanceOf<Solution.Yes>()
            solver.solveOnce(h, 1.seconds).shouldBeInstanceOf<Solution.Yes>()
            solver.solveOnce(h, probabilistic).probability shouldBe 0.6
        }

        test("solve/an-impossible-goal-has-probability-zero") {
            solver("0.6::h.").solveOnce("k".parseAsStruct(), probabilistic).probability shouldBe 0.0
        }

        test("copy/maps-the-theories-it-is-given") {
            val copy = solver("").copy(staticKb = "0.6::h.".parseAsTheory(operators))
            copy.solveOnce(h, probabilistic).probability shouldBe 0.6
        }

        test("clone/keeps-the-knowledge-base") {
            solver("0.6::h.").clone().solveOnce(h, probabilistic).probability shouldBe 0.6
        }
    })
