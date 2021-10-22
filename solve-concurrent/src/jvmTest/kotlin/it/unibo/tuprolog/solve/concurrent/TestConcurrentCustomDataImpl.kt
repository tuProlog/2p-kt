package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCustomDataImpl :
    TestConcurrentCustomData<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testApi() = multiRunConcurrentTest { super.testApi() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testEphemeralData() = multiRunConcurrentTest { super.testEphemeralData() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testDurableData() = multiRunConcurrentTest { super.testDurableData() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testPersistentData() = multiRunConcurrentTest { super.testPersistentData() }
}
