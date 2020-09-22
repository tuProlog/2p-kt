package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestFloatImpl(private val solverFactory: SolverFactory) : TestFloat {
    override fun testFloatDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFloatDecNeg() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(-3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFloatNat() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFloatAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFloatX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = float("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }
}
