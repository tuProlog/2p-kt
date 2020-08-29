package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestNotUnifyImpl(private val solverFactory: SolverFactory) : TestNotUnify {

    override fun testNumberNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testNumberXNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" notEqualsTo 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testXYNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" notEqualsTo "Y"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testDoubleNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (("X" notEqualsTo "Y") and ("X" notEqualsTo "abc"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testFDefNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "f"("X", "def") notEqualsTo ("f"("def", "Y"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testDiffNumberNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 2
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testDecNumberNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = intOf(1) notEqualsTo realOf(1.0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testGNotUnifyFX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("g"("X")) notEqualsTo ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) notEqualsTo ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFMultipleTermNotUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) notEqualsTo ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }
}