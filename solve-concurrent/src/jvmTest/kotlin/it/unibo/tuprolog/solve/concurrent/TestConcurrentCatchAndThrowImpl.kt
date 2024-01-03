package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCatchAndThrowImpl :
    TestConcurrentCatchAndThrow<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCatchThrow() = multiRunConcurrentTest { super.testCatchThrow() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCatchFail() = multiRunConcurrentTest { super.testCatchFail() }
}
