package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestCutImpl(private val solverFactory: SolverFactory) : TestCut{
    override fun testCut() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomOf("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testCutFailTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("!" and fail or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testCallCutFailTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (call("!") and fail or true )
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }
}