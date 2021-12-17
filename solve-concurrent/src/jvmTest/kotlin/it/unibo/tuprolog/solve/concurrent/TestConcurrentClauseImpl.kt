package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentClauseImpl :
    TestConcurrentClause<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseXBody() = multiRunConcurrentTest { super.testClauseXBody() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseAnyB() = multiRunConcurrentTest { super.testClauseAnyB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseNumB() = multiRunConcurrentTest { super.testClauseNumB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseFAnyNum() = multiRunConcurrentTest { super.testClauseFAnyNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseAtomBody() = multiRunConcurrentTest { super.testClauseAtomBody() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testClauseVariables() = multiRunConcurrentTest { super.testClauseVariables() }
}
