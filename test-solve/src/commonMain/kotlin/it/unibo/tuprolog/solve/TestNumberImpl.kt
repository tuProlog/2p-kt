package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestNumberImpl(private val solverFactory: SolverFactory) : TestNumber {
    override fun testBasicNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testDecNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testNegNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number(-3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testLetterNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testXNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }
}
