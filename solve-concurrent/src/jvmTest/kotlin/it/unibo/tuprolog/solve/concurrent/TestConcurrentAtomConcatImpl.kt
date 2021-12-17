package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomConcatImpl :
    TestConcurrentAtomConcat<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomConcatThirdIsVar() = multiRunConcurrentTest { super.testAtomConcatThirdIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomConcatFails() = multiRunConcurrentTest { super.testAtomConcatFails() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomConcatSecondIsVar() = multiRunConcurrentTest { super.testAtomConcatSecondIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomConcatFirstIsVar() = multiRunConcurrentTest { super.testAtomConcatFirstIsVar() }
}
