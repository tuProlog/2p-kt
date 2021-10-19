package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentFindAllImpl :
    TestConcurrentFindAll<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    override val errorSignature: Signature = Signature("ensure_executable", 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXInDiffValues() = multiRunConcurrentTest { super.testFindXInDiffValues() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindSumResult() = multiRunConcurrentTest { super.testFindSumResult() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXinFail() = multiRunConcurrentTest { super.testFindXinFail() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXinSameXValues() = multiRunConcurrentTest { super.testFindXinSameXValues() }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // @Test
    // override fun testResultListIsCorrect() = multiRunConcurrentTest { super.testResultListIsCorrect() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXtoDoubleAssignment() = multiRunConcurrentTest { super.testFindXtoDoubleAssignment() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXinGoal() = multiRunConcurrentTest { super.testFindXinGoal() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXinNumber() = multiRunConcurrentTest { super.testFindXinNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindXinCall() = multiRunConcurrentTest { super.testFindXinCall() }
}
