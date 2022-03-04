package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentStackTraceImpl :
    TestConcurrentStackTrace<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSimpleStackTrace() = multiRunConcurrentTest { super.testSimpleStackTrace() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDoubleStackTrace() = multiRunConcurrentTest { super.testDoubleStackTrace() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTripleStackTrace() = multiRunConcurrentTest { super.testTripleStackTrace() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testThrowIsNotInStacktrace() = multiRunConcurrentTest { super.testThrowIsNotInStacktrace() }
}
