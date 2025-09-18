package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestRepeatImpl(
    private val solverFactory: SolverFactory,
) : TestRepeat {
    override fun testRepeat() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (repeat and (cut and fail))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
