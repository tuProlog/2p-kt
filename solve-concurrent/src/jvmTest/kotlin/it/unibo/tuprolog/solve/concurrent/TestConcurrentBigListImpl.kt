package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentBigListImpl :
    TestConcurrentBigList<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    override val shortDuration: TimeDuration
        get() = super<TestConcurrentBigList>.shortDuration

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBigListGeneration() = multiRunConcurrentTest { super.testBigListGeneration() }
}
