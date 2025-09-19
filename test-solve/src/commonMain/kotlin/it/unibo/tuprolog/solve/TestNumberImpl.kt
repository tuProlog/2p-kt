package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestNumberImpl(
    private val solverFactory: SolverFactory,
) : TestNumber {
    override fun testBasicNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testDecNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNegNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(-3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testLetterNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testXNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
