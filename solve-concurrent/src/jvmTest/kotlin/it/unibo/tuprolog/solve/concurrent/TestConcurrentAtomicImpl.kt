package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomicImpl :
    TestConcurrentAtomic<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicAtom() = multiRunConcurrentTest { super.testAtomicAtom() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicAofB() = multiRunConcurrentTest { super.testAtomicAofB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicVar() = multiRunConcurrentTest { super.testAtomicVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicEmptyList() = multiRunConcurrentTest { super.testAtomicEmptyList() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicNum() = multiRunConcurrentTest { super.testAtomicNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomicNumDec() = multiRunConcurrentTest { super.testAtomicNumDec() }
}
