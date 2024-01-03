package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNumberCodesImpl :
    TestConcurrentNumberCodes<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesListIsVar() = multiRunConcurrentTest { super.testNumberCodesListIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesNumIsDecimal() = multiRunConcurrentTest { super.testNumberCodesNumIsDecimal() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesListIsVar2() = multiRunConcurrentTest { super.testNumberCodesListIsVar2() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesOk() = multiRunConcurrentTest { super.testNumberCodesOk() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesCompleteTest() = multiRunConcurrentTest { super.testNumberCodesCompleteTest() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesNegativeNumber() = multiRunConcurrentTest { super.testNumberCodesNegativeNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCodesChar() = multiRunConcurrentTest { super.testNumberCodesChar() }
}
