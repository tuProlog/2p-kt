package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentIntegerImpl :
    TestConcurrentInteger<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIntPositiveNum() = multiRunConcurrentTest { super.testIntPositiveNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIntNegativeNum() = multiRunConcurrentTest { super.testIntNegativeNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIntDecNum() = multiRunConcurrentTest { super.testIntDecNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIntX() = multiRunConcurrentTest { super.testIntX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIntAtom() = multiRunConcurrentTest { super.testIntAtom() }
}
