package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentTerm<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testTermDiff() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "\\=="(intOf(1), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "\\=="("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "\\=="(intOf(1), intOf(2))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "\\=="("X", intOf(1))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "\\=="("X", "Y")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "\\=="(`_`, `_`)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "\\=="("X", "a"("X"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "\\=="("f"("a"), "f"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testTermEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "=="(intOf(1), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "=="("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "=="(intOf(1), intOf(2))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "=="("X", intOf(1))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "=="("X", "Y")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "=="(`_`, `_`)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "=="("X", "a"("X"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "=="("f"("a"), "f"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testTermGreaterThan() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "@>"(realOf(1.0), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>"("aardvark", "zebra")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>"("short", "short")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>"("short", "shorter")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>"("foo"("b"), "foo"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@>"("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testTermGreaterThanEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "@>="(realOf(1.0), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>="("aardvark", "zebra")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>="("short", "short")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@>="("short", "shorter")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@>="("foo"("b"), "foo"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@>="("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@>="("foo"("a", "X"), "foo"("b", "Y"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testTermLessThan() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "@<"(realOf(1.0), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@<"("aardvark", "zebra")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@<"("short", "short")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@<"("short", "shorter")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@<"("foo"("b"), "foo"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@<"("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@<"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testTermLessThanEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = "@=<"(realOf(1.0), intOf(1))
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"(realOf(1.1), realOf(1.1))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"(realOf(1.0), realOf(1.2))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"(intOf(1), intOf(1))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"(intOf(1), intOf(2))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"("aardvark", "zebra")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"("short", "short")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"("short", "shorter")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"("foo"("b"), "foo"("a"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "@=<"("X", "X")
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "@=<"("foo"("a", "X"), "foo"("b", "Y"))
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
