package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCopyAbstractTerm :
    TestConcurrentCopyTerm<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyXNum() = multiRunConcurrentTest { super.testCopyXNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyAnyA() = multiRunConcurrentTest { super.testCopyAnyA() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopySum() = multiRunConcurrentTest { super.testCopySum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyAnyAny() = multiRunConcurrentTest { super.testCopyAnyAny() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyTripleSum() = multiRunConcurrentTest { super.testCopyTripleSum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyAA() = multiRunConcurrentTest { super.testCopyAA() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyAB() = multiRunConcurrentTest { super.testCopyAB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCopyF() = multiRunConcurrentTest { super.testCopyF() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDoubleCopy() = multiRunConcurrentTest { super.testDoubleCopy() }
}
