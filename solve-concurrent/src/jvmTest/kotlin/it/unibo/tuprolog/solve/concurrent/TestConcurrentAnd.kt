package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAnd<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testTermIsFreeVariable() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
            val query = "X" eq 1 and `var`("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testWithSubstitution() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
            val query = `var`("X") and ("X" eq 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testFailIsCallable() { // goal
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
            val query = fail and call(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNoFooIsCallable() {
        logicProgramming {
            val solver =
                solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.ERROR),
                )

            val query = "nofoo"("X") and call("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("nofoo", 1),
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testTrueVarCallable() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "X" eq true and call("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to true))

            expected.assertingEquals(solutions)
        }
    }
}
