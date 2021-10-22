package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNotProvableImpl :
    TestConcurrentNotProvable<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    override val errorSignature: Signature = Signature("ensure_executable", 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPTrue() = multiRunConcurrentTest { super.testNPTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPCut() = multiRunConcurrentTest { super.testNPCut() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPCutFail() = multiRunConcurrentTest { super.testNPCutFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testOrNotCutFail() = multiRunConcurrentTest { super.testOrNotCutFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPEquals() = multiRunConcurrentTest { super.testNPEquals() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPNum() = multiRunConcurrentTest { super.testNPNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNPX() = multiRunConcurrentTest { super.testNPX() }
}
