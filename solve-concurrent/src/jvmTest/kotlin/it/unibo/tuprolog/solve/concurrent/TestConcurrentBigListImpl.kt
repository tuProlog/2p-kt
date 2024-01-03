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
        get() = 6000

    override val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    override val longDuration: TimeDuration
        get() = 4 * mediumDuration

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBigListGeneration() = multiRunConcurrentTest(1) { super.testBigListGeneration() }
}
