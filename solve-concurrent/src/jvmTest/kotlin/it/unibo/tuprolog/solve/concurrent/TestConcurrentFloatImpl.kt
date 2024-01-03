package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentFloatImpl :
    TestConcurrentFloat<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFloatDec() = multiRunConcurrentTest { super.testFloatDec() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFloatDecNeg() = multiRunConcurrentTest { super.testFloatDecNeg() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFloatNat() = multiRunConcurrentTest { super.testFloatNat() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFloatAtom() = multiRunConcurrentTest { super.testFloatAtom() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFloatX() = multiRunConcurrentTest { super.testFloatX() }
}
