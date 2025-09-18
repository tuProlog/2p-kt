package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestNotUnifyImpl(
    private val solverFactory: SolverFactory,
) : TestNotUnify {
    override fun testNumberNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testNumberXNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" notEqualsTo 1
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testXYNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" notEqualsTo "Y"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testDoubleNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (("X" notEqualsTo "Y") and ("X" notEqualsTo "abc"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFDefNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "f"("X", "def") notEqualsTo ("f"("def", "Y"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testDiffNumberNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 2
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testDecNumberNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = intOf(1) notEqualsTo realOf(1.0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testGNotUnifyFX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("g"("X")) notEqualsTo ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) notEqualsTo ("f"("a"("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFMultipleTermNotUnify() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) notEqualsTo ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }
}
