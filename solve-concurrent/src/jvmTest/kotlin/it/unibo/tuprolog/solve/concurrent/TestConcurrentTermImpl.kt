package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentTermImpl :
    TestConcurrentTerm<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermDiff() = multiRunConcurrentTest { super.testTermDiff() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermEq() = multiRunConcurrentTest { super.testTermEq() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermGreaterThan() = multiRunConcurrentTest { super.testTermGreaterThan() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermGreaterThanEq() = multiRunConcurrentTest { super.testTermGreaterThanEq() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermLessThan() = multiRunConcurrentTest { super.testTermLessThan() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermLessThanEq() = multiRunConcurrentTest { super.testTermLessThanEq() }
}
