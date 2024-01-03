package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentIsImpl :
    TestConcurrentIs<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsResult() = multiRunConcurrentTest { super.testIsResult() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsXY() = multiRunConcurrentTest { super.testIsXY() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsFoo() = multiRunConcurrentTest { super.testIsFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsNNumber() = multiRunConcurrentTest { super.testIsNNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsNumberFoo() = multiRunConcurrentTest { super.testIsNumberFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIsXFloat() = multiRunConcurrentTest { super.testIsXFloat() }
}
