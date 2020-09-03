package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

class TestCopyTermImpl(private val solverFactory: SolverFactory) : TestCopyTerm {
    override fun testCopyXNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("X", 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testCopyAnyA() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"(`_`, "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testCopySum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("a"+"X", "X"+"b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes("X" to "a")),
                    solutions
            )
        }
    }

    override fun testCopyAnyAny() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"(`_`, `_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testCopyTripleSum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("X"+"X"+"Y", "A"+"B"+"B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes("B" to "A")),
                    solutions
            )
        }
    }

    override fun testCopyAA() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("a", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testCopyAB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("a", "b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.no()),
                    solutions
            )
        }
    }

    override fun testCopyF() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "copy_term"("f"("a"), "f"("X"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.yes("X" to "a")),
                    solutions
            )
        }
    }

    override fun testDoubleCopy() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("copy_term"("a"+"X", "X"+"b") and  "copy_term"("a"+"X", "X"+"b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.no()),
                    solutions
            )
        }
    }
}