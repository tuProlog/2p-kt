package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentArithImpl :
    TestConcurrentArith<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithDiff() = multiRunConcurrentTest { super.testArithDiff() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithEq() = multiRunConcurrentTest { super.testArithEq() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithGreaterThan() = multiRunConcurrentTest { super.testArithGreaterThan() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithGreaterThanEq() = multiRunConcurrentTest { super.testArithGreaterThanEq() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithLessThan() = multiRunConcurrentTest { super.testArithLessThan() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArithLessThanEq() = multiRunConcurrentTest { super.testArithLessThanEq() }
}
