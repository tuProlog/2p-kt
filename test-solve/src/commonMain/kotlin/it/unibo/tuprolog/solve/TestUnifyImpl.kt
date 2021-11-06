package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestUnifyImpl(private val solverFactory: SolverFactory) : TestUnify {

    override fun testNumberUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testNumberXUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" eq 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testXYUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" eq "Y"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to "Y")),
                solutions
            )
        }
    }

    override fun testDoubleUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (("X" eq "Y") and ("X" eq "abc"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to "abc", "Y" to "abc")),
                solutions
            )
        }
    }

    override fun testFDefUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "f"("X", "def") eq "f"("def", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to "def", "Y" to "def")),
                solutions
            )
        }
    }

    override fun testDiffNumberUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq 2
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testDecNumberUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq realOf(1.0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testGUnifyFX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("g"("X")) eq ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) eq ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFMultipleTermUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) eq ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testMultipleTermUnify() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("A", "B", "C")) eq ("f"("g"("B", "B"), "g"("C", "C"), "g"("D", "D")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes(
                        "A" to "g"("g"("g"("D", "D"), "g"("D", "D")), "g"("g"("D", "D"), "g"("D", "D"))),
                        "B" to "g"("g"("D", "D"), "g"("D", "D")),
                        "C" to "g"("D", "D")
                    )
                ),
                solutions
            )
        }
    }
}
