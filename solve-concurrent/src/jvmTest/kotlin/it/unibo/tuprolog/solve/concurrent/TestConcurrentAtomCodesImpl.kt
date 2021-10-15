package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomCodesImpl :
    TestConcurrentAtomCodes<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomCodesSecondIsVar1() = multiRunConcurrentTest { super.testAtomCodesSecondIsVar1() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomCodesSecondIsVar() = multiRunConcurrentTest { super.testAtomCodesSecondIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomCodesFirstIsVar() = multiRunConcurrentTest { super.testAtomCodesFirstIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomCodesNoVar() = multiRunConcurrentTest { super.testAtomCodesNoVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomCodesFail() = multiRunConcurrentTest { super.testAtomCodesFail() }
}
