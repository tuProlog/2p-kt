package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCallImpl :
    TestConcurrentCall<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    override val errorSignature: Signature = Signature("ensure_executable", 1)

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testCallCut() = multiRunConcurrentTest { super.testCallCut() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallFail() = multiRunConcurrentTest { super.testCallFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallFailX() = multiRunConcurrentTest { super.testCallFailX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallFailCall() = multiRunConcurrentTest { super.testCallFailCall() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testCallWriteX() = multiRunConcurrentTest { super.testCallWriteX() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testCallWriteCall() = multiRunConcurrentTest { super.testCallWriteCall() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallX() = multiRunConcurrentTest { super.testCallX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallOne() = multiRunConcurrentTest { super.testCallOne() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallFailOne() = multiRunConcurrentTest { super.testCallFailOne() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testCallWriteOne() = multiRunConcurrentTest { super.testCallWriteOne() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallTrue() = multiRunConcurrentTest { super.testCallTrue() }
}
