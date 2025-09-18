package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestCompoundImpl(
    private val solverFactory: SolverFactory,
) : TestCompound {
    override fun testCompoundDec() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCompoundNegDec() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(-33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCompoundNegA() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("-"("a"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCompoundAny() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCompoundA() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCompoundAOfB() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCompoundListA() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(logicListOf("a"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }
}
