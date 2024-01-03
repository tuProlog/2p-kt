package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCompoundImpl :
    TestConcurrentCompound<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundDec() = multiRunConcurrentTest { super.testCompoundDec() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundNegDec() = multiRunConcurrentTest { super.testCompoundNegDec() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundNegA() = multiRunConcurrentTest { super.testCompoundNegA() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundAny() = multiRunConcurrentTest { super.testCompoundAny() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundA() = multiRunConcurrentTest { super.testCompoundA() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundAOfB() = multiRunConcurrentTest { super.testCompoundAOfB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCompoundListA() = multiRunConcurrentTest { super.testCompoundListA() }
}
