package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNumberCharsImpl :
    TestConcurrentNumberChars<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsListIsVar() = multiRunConcurrentTest { super.testNumberCharsListIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsOK() = multiRunConcurrentTest { super.testNumberCharsOK() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsNumIsVar() = multiRunConcurrentTest { super.testNumberCharsNumIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsNumNegativeIsVar() = multiRunConcurrentTest { super.testNumberCharsNumNegativeIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsSpace() = multiRunConcurrentTest { super.testNumberCharsSpace() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsDecimalNumber() = multiRunConcurrentTest { super.testNumberCharsDecimalNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsCompleteCase() = multiRunConcurrentTest { super.testNumberCharsCompleteCase() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberCharsInstantiationError() =
        multiRunConcurrentTest { super.testNumberCharsInstantiationError() }
}
