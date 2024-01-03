package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNotUnifyImpl :
    TestConcurrentNotUnify<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberNotUnify() = multiRunConcurrentTest { super.testNumberNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumberXNotUnify() = multiRunConcurrentTest { super.testNumberXNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testXYNotUnify() = multiRunConcurrentTest { super.testXYNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDoubleNotUnify() = multiRunConcurrentTest { super.testDoubleNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFDefNotUnify() = multiRunConcurrentTest { super.testFDefNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDiffNumberNotUnify() = multiRunConcurrentTest { super.testDiffNumberNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDecNumberNotUnify() = multiRunConcurrentTest { super.testDecNumberNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testGNotUnifyFX() = multiRunConcurrentTest { super.testGNotUnifyFX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFNotUnify() = multiRunConcurrentTest { super.testFNotUnify() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFMultipleTermNotUnify() = multiRunConcurrentTest { super.testFMultipleTermNotUnify() }
}
