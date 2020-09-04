package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

class TestRepeatImpl(private val solverFactory: SolverFactory) : TestRepeat {
    override fun testRepeat() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (repeat and "!" and fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.no()),
                    solutions
            )
        }
    }
}