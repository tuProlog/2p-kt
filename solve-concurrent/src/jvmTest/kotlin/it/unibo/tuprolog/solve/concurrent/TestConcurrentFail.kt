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

interface TestConcurrentFail<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = fail
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testUndefPred() { // streams solver: `No(query=undef_pred)` instead of undef_pred/0
        logicProgramming {
            val solver =
                solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.ERROR),
                )

            val query = atomOf("undef_pred")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("undef_pred", 0),
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testSetFlagFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ("set_prolog_flag"("unknown" and fail) and "undef_pred")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testSetFlagWarning() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ("set_prolog_flag"("unknown" and "warning") and "undef_pred")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
