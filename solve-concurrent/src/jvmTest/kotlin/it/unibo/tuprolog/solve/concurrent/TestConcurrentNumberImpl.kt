package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNumberImpl :
    TestConcurrentNumber<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBasicNum() = multiRunConcurrentTest { super.testBasicNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDecNum() = multiRunConcurrentTest { super.testDecNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNegNum() = multiRunConcurrentTest { super.testNegNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testLetterNum() = multiRunConcurrentTest { super.testLetterNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testXNum() = multiRunConcurrentTest { super.testXNum() }
}
