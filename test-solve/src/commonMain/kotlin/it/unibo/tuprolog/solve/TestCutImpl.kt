package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestCutImpl(
    private val solverFactory: SolverFactory,
) : TestCut {
    override fun testCut() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomOf("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCutFailTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("!" and fail or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCallCutFailTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (call("!") and fail or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }
}
