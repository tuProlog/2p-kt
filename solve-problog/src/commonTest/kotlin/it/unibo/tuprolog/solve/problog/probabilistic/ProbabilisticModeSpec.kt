package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.bdd.countVariableNodes
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.binaryDecisionDiagram
import it.unibo.tuprolog.solve.hasBinaryDecisionDiagram
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProbabilisticModeSpec :
    FunSpec({
        fun createSolver(): Solver {
            val theory =
                """
                0.6::heads(C) :- coin(C).
                coin(c1).
                coin(c2).
                coin(c3).
                coin(c4).
                someHeads :- heads(_).
            """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            val solver = ProblogSolverFactory.mutableSolverWithDefaultBuiltins()
            solver.loadStaticKb(theory)
            return solver
        }

        test("probabilistic-mode-disabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(false)
            val solutions = solver.solve(query, solveOptions).toList()

            assertEquals(solutions.size, 4)
            solutions.forEach {
                assertEquals(it.solvedQuery, query)
                TestUtils.assertEqualsDouble(solutions[0].probability, 1.0, 0.0)
            }
        }

        test("probabilistic-mode-enabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(true)
            val solutions = solver.solve(query, solveOptions).toList()

            assertEquals(solutions.size, 1)
            assertEquals(solutions[0].solvedQuery, query)
            TestUtils.assertEqualsDouble(solutions[0].probability, 0.9744)
        }

        test("binary-decision-diagram-probabilistic-mode-disabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(false)
            val solutions = solver.solve(query, solveOptions).toList()

            assertEquals(solutions.size, 4)
            solutions.forEach {
                assertFalse(it.hasBinaryDecisionDiagram)
                assertNull(it.binaryDecisionDiagram)
            }
        }

        test("binary-decision-diagram-probabilistic-mode-enabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(true)
            val solutions = solver.solve(query, solveOptions).toList()

            assertEquals(solutions.size, 1)
            assertTrue(solutions[0].hasBinaryDecisionDiagram)
            assertNotNull(solutions[0].binaryDecisionDiagram)
            assertEquals(
                solutions[0].binaryDecisionDiagram!!.countVariableNodes(),
                4,
            )
        }
    })
