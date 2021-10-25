package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import kotlin.test.Test

class TestConcurrentOrImpl :
    TestConcurrentOr<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTrueOrFalse() = multiRunConcurrentTest { super.testTrueOrFalse() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific primitive ! (cut) for concurrent solver
    override fun testCutFalseOrTrue() = multiRunConcurrentTest { super.testCutFalseOrTrue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific primitive ! (cut) for concurrent solver
    override fun testCutCall() = multiRunConcurrentTest { super.testCutCall() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo implement specific primitive ! (cut) for concurrent solver
    override fun testCutAssignedValue() = multiRunConcurrentTest { super.testCutAssignedValue() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testOrDoubleAssignment() = multiRunConcurrentTest { super.testOrDoubleAssignment() }
}
