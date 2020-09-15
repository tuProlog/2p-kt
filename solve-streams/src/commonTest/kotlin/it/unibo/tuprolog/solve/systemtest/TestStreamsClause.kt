package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestClause
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsClause : TestClause, SolverFactory by StreamsSolverFactory {
    private val prototype = TestClause.prototype(this)

    @Test
    override fun testClauseXBody() {
        prototype.testClauseXBody()
    }

    @Test
    override fun testClauseAnyB() {
        prototype.testClauseAnyB()
    }

    @Test
    override fun testClauseNumB() {
        prototype.testClauseNumB()
    }

    @Test
    override fun testClauseFAnyNum() {
        prototype.testClauseFAnyNum()
    }

    @Test
    override fun testClauseAtomBody() {
        prototype.testClauseAtomBody()
    }
}
