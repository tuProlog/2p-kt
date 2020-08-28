package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestIntegerImpl(private val solverFactory: SolverFactory) : TestInteger{
    override fun testIntPositiveNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = integer(3)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testIntNegativeNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = integer(-3)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testIntDecNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = integer(3.3)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testIntX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = integer("X")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testIntAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = integer("atom")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }
}