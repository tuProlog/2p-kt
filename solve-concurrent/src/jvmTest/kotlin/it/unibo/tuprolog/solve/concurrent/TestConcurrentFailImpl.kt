package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentFailImpl :
    TestConcurrentFail<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFail() = multiRunConcurrentTest { super.testFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testUndefPred() = multiRunConcurrentTest { super.testUndefPred() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetFlagFail() = multiRunConcurrentTest { super.testSetFlagFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetFlagWarning() = multiRunConcurrentTest { super.testSetFlagWarning() }
}
