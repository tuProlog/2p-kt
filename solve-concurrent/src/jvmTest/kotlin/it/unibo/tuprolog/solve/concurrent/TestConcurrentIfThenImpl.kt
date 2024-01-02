package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import kotlin.test.Test

class TestConcurrentIfThenImpl :
    TestConcurrentIfThen<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenTrue() = multiRunConcurrentTest { super.testIfThenTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenFail() = multiRunConcurrentTest { super.testIfThenFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenFailTrue() = multiRunConcurrentTest { super.testIfThenFailTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenXtoOne() = multiRunConcurrentTest { super.testIfThenXtoOne() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific rule -> (arrow) for concurrent solver
    override fun testIfThenXOr() = multiRunConcurrentTest { super.testIfThenXOr() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenOrWithDoubleSub() = multiRunConcurrentTest { super.testIfThenOrWithDoubleSub() }
}
