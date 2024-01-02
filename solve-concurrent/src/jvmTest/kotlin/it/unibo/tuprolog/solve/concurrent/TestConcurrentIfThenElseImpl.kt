package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import kotlin.test.Test

class TestConcurrentIfThenElseImpl :
    TestConcurrentIfThenElse<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfTrueElseFail() = multiRunConcurrentTest { super.testIfTrueElseFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific rule -> (arrow) for concurrent solver
    override fun testIfFailElseTrue() = multiRunConcurrentTest { super.testIfFailElseTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfTrueThenElseFail() = multiRunConcurrentTest { super.testIfTrueThenElseFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfFailElseFail() = multiRunConcurrentTest { super.testIfFailElseFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfXTrueElseX() = multiRunConcurrentTest { super.testIfXTrueElseX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific rule -> (arrow) for concurrent solver
    override fun testIfFailElseX() = multiRunConcurrentTest { super.testIfFailElseX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenElseOrWithDoubleSub() = multiRunConcurrentTest { super.testIfThenElseOrWithDoubleSub() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // nondeterministic order, this test may pass or fail unpredictably. It's the Schrödinger's test
    override fun testIfOrElseTrue() = multiRunConcurrentTest { super.testIfOrElseTrue() }
}
