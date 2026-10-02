package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestClause
import kotlin.test.Test

class TestPrologClause :
    TestClause,
    SolverFactory by PrologSolverFactory {
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

    @Test
    override fun testClauseVariables() {
        prototype.testClauseVariables()
    }
}
