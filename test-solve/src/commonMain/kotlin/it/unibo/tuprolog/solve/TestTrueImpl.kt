package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestTrueImpl(private val solverFactory: SolverFactory) : TestTrue {
    override fun testTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomOf("true")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes()),
                    solutions
            )
        }
    }
}