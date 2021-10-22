package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentOrImpl :
    TestConcurrentOr<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTrueOrFalse() = multiRunConcurrentTest { super.testTrueOrFalse() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCutFalseOrTrue() = multiRunConcurrentTest { super.testCutFalseOrTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCutCall() = multiRunConcurrentTest { super.testCutCall() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCutAssignedValue() = multiRunConcurrentTest { super.testCutAssignedValue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testOrDoubleAssignment() = multiRunConcurrentTest { super.testOrDoubleAssignment() }
}
