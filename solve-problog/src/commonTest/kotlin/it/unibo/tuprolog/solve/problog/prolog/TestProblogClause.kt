package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestClause
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestProblogClause :
    TestClause,
    SolverFactory by ProblogSolverFactory {
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

    // NOTE: This relies on the internal knowledge base representation. Should we consider this a bug?
    @Ignore
    @Test
    override fun testClauseVariables() {
        prototype.testClauseVariables()
    }
}
