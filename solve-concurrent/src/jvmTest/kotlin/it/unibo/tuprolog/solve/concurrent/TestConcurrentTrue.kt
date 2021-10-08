package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.yes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

interface TestConcurrentTrue<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atomOf("true")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(sequenceOf(query.yes()))

            expected.assertingEquals(solutions)
        }
    }
}

class TestConcurrentTrueImpl :
    TestConcurrentTrue<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTrue() = multiRunConcurrentTest { super.testTrue() }
}
