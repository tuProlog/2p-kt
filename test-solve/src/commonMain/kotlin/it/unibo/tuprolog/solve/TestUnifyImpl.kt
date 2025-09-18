package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestUnifyImpl(
    private val solverFactory: SolverFactory,
) : TestUnify {
    override fun testNumberUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNumberXUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" eq 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    override fun testXYUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" eq "Y"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "Y")),
                solutions,
            )
        }
    }

    override fun testDoubleUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (("X" eq "Y") and ("X" eq "abc"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "abc", "Y" to "abc")),
                solutions,
            )
        }
    }

    override fun testFDefUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "f"("X", "def") eq "f"("def", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "def", "Y" to "def")),
                solutions,
            )
        }
    }

    override fun testDiffNumberUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq 2
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testDecNumberUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 eq realOf(1.0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testGUnifyFX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("g"("X")) eq ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) eq ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFMultipleTermUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) eq ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testMultipleTermUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("A", "B", "C")) eq ("f"("g"("B", "B"), "g"("C", "C"), "g"("D", "D")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(
                        "A" to "g"("g"("g"("D", "D"), "g"("D", "D")), "g"("g"("D", "D"), "g"("D", "D"))),
                        "B" to "g"("g"("D", "D"), "g"("D", "D")),
                        "C" to "g"("D", "D"),
                    ),
                ),
                solutions,
            )
        }
    }
}
