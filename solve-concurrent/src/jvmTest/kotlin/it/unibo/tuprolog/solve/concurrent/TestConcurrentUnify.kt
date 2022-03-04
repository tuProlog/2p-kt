package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentUnify<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testNumberUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = 1 eq 1
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberXUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "X" eq 1
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testXYUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "X" eq "Y"
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "Y"))

            expected.assertingEquals(solutions)
        }
    }

    fun testDoubleUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = (("X" eq "Y") and ("X" eq "abc"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "abc", "Y" to "abc"))

            expected.assertingEquals(solutions)
        }
    }

    fun testFDefUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "f"("X", "def") eq "f"("def", "Y")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "def", "Y" to "def"))

            expected.assertingEquals(solutions)
        }
    }

    fun testDiffNumberUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = 1 eq 2
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testDecNumberUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = 1 eq realOf(1.0)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testGUnifyFX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("g"("X")) eq ("f"("a"("X")))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) eq ("f"("a"("X")))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFMultipleTermUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) eq ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testMultipleTermUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("f"("A", "B", "C")) eq ("f"("g"("B", "B"), "g"("C", "C"), "g"("D", "D")))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.yes(
                    "A" to "g"("g"("g"("D", "D"), "g"("D", "D")), "g"("g"("D", "D"), "g"("D", "D"))),
                    "B" to "g"("g"("D", "D"), "g"("D", "D")),
                    "C" to "g"("D", "D")
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
