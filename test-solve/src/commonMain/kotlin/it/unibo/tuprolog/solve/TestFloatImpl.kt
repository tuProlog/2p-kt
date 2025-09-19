package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestFloatImpl(
    private val solverFactory: SolverFactory,
) : TestFloat {
    override fun testFloatDec() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFloatDecNeg() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(-3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFloatNat() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFloatAtom() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFloatX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
