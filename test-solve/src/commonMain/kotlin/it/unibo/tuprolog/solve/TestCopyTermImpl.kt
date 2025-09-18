package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

class TestCopyTermImpl(
    private val solverFactory: SolverFactory,
) : TestCopyTerm {
    override fun testCopyXNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(X, 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCopyAnyA() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(`_`, "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCopySum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(atomOf("a") + X, X + "b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes(X to "a")),
                solutions,
            )
        }
    }

    override fun testCopyAnyAny() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(`_`, `_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCopyTripleSum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(X + X + Y, A + B + B)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes(A to B)),
                solutions,
            )
        }
    }

    override fun testCopyAA() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term("a", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCopyAB() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term("a", "b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCopyF() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term("f"("a"), "f"(X))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes(X to "a")),
                solutions,
            )
        }
    }

    override fun testDoubleCopy() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = copy_term(atom("a") + X, X + "b") and copy_term(atom("a") + X, X + "b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
