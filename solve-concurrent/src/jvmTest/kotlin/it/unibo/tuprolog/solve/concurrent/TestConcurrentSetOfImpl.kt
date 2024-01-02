package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentSetOfImpl :
    TestConcurrentSetOf<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfBasic() = multiRunConcurrentTest { super.testSetOfBasic() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfX() = multiRunConcurrentTest { super.testSetOfX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfNoSorted() = multiRunConcurrentTest { super.testSetOfNoSorted() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfDoubled() = multiRunConcurrentTest { super.testSetOfDoubled() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfFail() = multiRunConcurrentTest { super.testSetOfFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSetOfAsFindAll() = multiRunConcurrentTest { super.testSetOfAsFindAll() }
}
