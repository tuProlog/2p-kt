package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentBagOfImpl :
    TestConcurrentBagOf<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagXInDifferentValues() = multiRunConcurrentTest { super.testBagXInDifferentValues() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfFindX() = multiRunConcurrentTest { super.testBagOfFindX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfYXZ() = multiRunConcurrentTest { super.testBagOfYXZ() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfFail() = multiRunConcurrentTest { super.testBagOfFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfSameAsFindall() = multiRunConcurrentTest { super.testBagOfSameAsFindall() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfInstanceError() = multiRunConcurrentTest { super.testBagOfInstanceError() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBagOfTypeError() = multiRunConcurrentTest { super.testBagOfTypeError() }
}
