package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomLengthImpl :
    TestConcurrentAtomLength<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomLengthNoVar() = multiRunConcurrentTest { super.testAtomLengthNoVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomLengthSecondIsVar() = multiRunConcurrentTest { super.testAtomLengthSecondIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomLengthFirstIsVar() = multiRunConcurrentTest { super.testAtomLengthFirstIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomLengthSecondIsVar2() = multiRunConcurrentTest { super.testAtomLengthSecondIsVar2() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomLengthFail() = multiRunConcurrentTest { super.testAtomLengthFail() }
}
