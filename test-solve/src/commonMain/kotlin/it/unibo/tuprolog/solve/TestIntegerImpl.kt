package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestIntegerImpl(
    private val solverFactory: SolverFactory,
) : TestInteger {
    override fun testIntPositiveNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = integer(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testIntNegativeNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = integer(intOf(-3))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testIntDecNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = integer(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIntX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = integer("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIntAtom() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = integer("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }
}
