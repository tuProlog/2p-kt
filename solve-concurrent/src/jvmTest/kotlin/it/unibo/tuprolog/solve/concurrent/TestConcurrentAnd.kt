package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

interface TestConcurrentAnd<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testTermIsFreeVariable() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = "X" `=` 1 and `var`("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
//            assertEquals(
//                fromSequence(query.no()),
//                fromSequence(solutions)
//            )
        }
    }

    fun testWithSubstitution() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = `var`("X") and ("X" `=` 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
//            assertEquals(
//                expected,
//                solutions,
//                "Expected: $expected, Actual: $solutions"
//            )
        }
    }

    fun testFailIsCallable() { // goal
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = fail and call(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNoFooIsCallable() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                flags = FlagStore.of(Unknown to Unknown.ERROR)
            )

            val query = "nofoo"("X") and call("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    ExistenceError.forProcedure(
                        DummyInstances.executionContext,
                        Signature("nofoo", 1)
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testTrueVarCallable() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "X" `=` true and call("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to true))

            expected.assertingEquals(solutions)
        }
    }
}

class TestConcurrentAndImpl :
    TestConcurrentAnd<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermIsFreeVariable() = multiRunConcurrentTest { super.testTermIsFreeVariable() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testWithSubstitution() = multiRunConcurrentTest { super.testWithSubstitution() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFailIsCallable() = multiRunConcurrentTest { super.testFailIsCallable() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNoFooIsCallable() = multiRunConcurrentTest { super.testNoFooIsCallable() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testTrueVarCallable() = multiRunConcurrentTest { super.testTrueVarCallable() }
}
