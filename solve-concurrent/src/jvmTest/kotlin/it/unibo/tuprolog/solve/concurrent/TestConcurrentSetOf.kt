package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentSetOf<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testSetOfBasic() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testSetOfX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 1) or ("X" eq 2), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testSetOfNoSorted() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 2) or ("X" eq 1), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testSetOfDoubled() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 2) or ("X" eq 2), "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testSetOfFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testSetOfAsFindAll() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = setof("X", "Y" sup ((("X" eq 1) or ("Y" eq 1)) or (("X" eq 2) or ("Y" eq 2))), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }
}
