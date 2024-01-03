package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentCharCodeImpl :
    TestConcurrentCharCode<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCharCodeSecondIsVar() = multiRunConcurrentTest { super.testCharCodeSecondIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCharCodeFirstIsVar() = multiRunConcurrentTest { super.testCharCodeFirstIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCharCodeTypeError() = multiRunConcurrentTest { super.testCharCodeTypeError() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCharCodeFails() = multiRunConcurrentTest { super.testCharCodeFails() }
}
