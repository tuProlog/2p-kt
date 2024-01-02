package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAndImpl :
    TestConcurrentAnd<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermIsFreeVariable() = multiRunConcurrentTest { super.testTermIsFreeVariable() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testWithSubstitution() = multiRunConcurrentTest { super.testWithSubstitution() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFailIsCallable() = multiRunConcurrentTest { super.testFailIsCallable() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNoFooIsCallable() = multiRunConcurrentTest { super.testNoFooIsCallable() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTrueVarCallable() = multiRunConcurrentTest { super.testTrueVarCallable() }
}
