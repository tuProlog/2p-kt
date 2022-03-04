package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNonVar<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testNonVarNumber() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = nonvar(33.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNonVarFoo() { // foo
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = nonvar("foo")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNonVarFooCl() { // Foo (CapsLock)
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = nonvar("Foo")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNonVarFooAssignment() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("foo" eq "Foo") and nonvar("Foo")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("Foo" to "foo"))

            expected.assertingEquals(solutions)
        }
    }

    fun testNonVarAnyTerm() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = nonvar(`_`)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNonVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = nonvar("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
