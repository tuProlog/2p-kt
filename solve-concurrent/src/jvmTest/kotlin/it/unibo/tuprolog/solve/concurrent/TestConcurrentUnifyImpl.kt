package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentUnifyImpl :
    TestConcurrentUnify<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberUnify() = multiRunConcurrentTest { super.testNumberUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberXUnify() = multiRunConcurrentTest { super.testNumberXUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testXYUnify() = multiRunConcurrentTest { super.testXYUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDoubleUnify() = multiRunConcurrentTest { super.testDoubleUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFDefUnify() = multiRunConcurrentTest { super.testFDefUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDiffNumberUnify() = multiRunConcurrentTest { super.testDiffNumberUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDecNumberUnify() = multiRunConcurrentTest { super.testDecNumberUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testGUnifyFX() = multiRunConcurrentTest { super.testGUnifyFX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFUnify() = multiRunConcurrentTest { super.testFUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFMultipleTermUnify() = multiRunConcurrentTest { super.testFMultipleTermUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testMultipleTermUnify() = multiRunConcurrentTest { super.testMultipleTermUnify() }
}
