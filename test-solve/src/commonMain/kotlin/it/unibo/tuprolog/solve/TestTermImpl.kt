package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestTermImpl(
    private val solverFactory: SolverFactory,
) : TestTerm {
    override fun testTermDiff() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "\\=="(intOf(1), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "\\=="("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "\\=="(intOf(1), intOf(2))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "\\=="("X", intOf(1))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "\\=="("X", "Y")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "\\=="(`_`, `_`)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "\\=="("X", "a"("X"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "\\=="("f"("a"), "f"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testTermEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "=="(intOf(1), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "=="("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "=="(intOf(1), intOf(2))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "=="("X", intOf(1))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "=="("X", "Y")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "=="(`_`, `_`)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "=="("X", "a"("X"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "=="("f"("a"), "f"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testTermGreaterThan() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "@>"(realOf(1.0), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>"("aardvark", "zebra")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>"("short", "short")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>"("short", "shorter")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>"("foo"("b"), "foo"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@>"("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testTermGreaterThanEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "@>="(realOf(1.0), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>="("aardvark", "zebra")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>="("short", "short")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@>="("short", "shorter")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@>="("foo"("b"), "foo"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@>="("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@>="("foo"("a", "X"), "foo"("b", "Y"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testTermLessThan() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "@<"(realOf(1.0), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@<"("aardvark", "zebra")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@<"("short", "short")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@<"("short", "shorter")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@<"("foo"("b"), "foo"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@<"("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@<"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testTermLessThanEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "@=<"(realOf(1.0), intOf(1))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"(realOf(1.1), realOf(1.1))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"(realOf(1.0), realOf(1.2))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"(intOf(1), intOf(1))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"(intOf(1), intOf(2))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"("aardvark", "zebra")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"("short", "short")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"("short", "shorter")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"("foo"("b"), "foo"("a"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )

            query = "@=<"("X", "X")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )

            query = "@=<"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }
}
