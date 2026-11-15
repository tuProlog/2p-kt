package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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

            4 shouldBe solutions.size
            solutions.forEach {
                query shouldBe it.solvedQuery?.body
                it.probability shouldBe 1.0
            }
        }

        test("probabilistic-mode-enabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(true)
            val solutions = solver.solve(query, solveOptions).toList()

            1 shouldBe solutions.size
            query shouldBe solutions[0].solvedQuery?.body
            val probability = solutions[0].probability
            withClue(
                "probability $probability is not 0.9744",
            ) { TestUtils.assertEqualsDouble(probability, 0.9744).shouldBeTrue() }
        }

        test("binary-decision-diagram-probabilistic-mode-disabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(false)
            val solutions = solver.solve(query, solveOptions).toList()

            4 shouldBe solutions.size
            solutions.forEach {
                it.hasBinaryDecisionDiagram.shouldBeFalse()
                it.binaryDecisionDiagram.shouldBeNull()
            }
        }

        test("binary-decision-diagram-probabilistic-mode-enabled") {
            val solver = createSolver()
            val query = "someHeads".parseAsStruct()
            val solveOptions = SolveOptions.DEFAULT.setProbabilistic(true)
            val solutions = solver.solve(query, solveOptions).toList()

            1 shouldBe solutions.size
            solutions[0].hasBinaryDecisionDiagram.shouldBeTrue()
            solutions[0].binaryDecisionDiagram.shouldNotBeNull()
            4 shouldBe solutions[0].binaryDecisionDiagram!!.countVariableNodes()
        }
    })
