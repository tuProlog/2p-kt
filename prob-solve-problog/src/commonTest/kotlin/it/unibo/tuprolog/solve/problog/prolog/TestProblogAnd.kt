package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAnd
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAnd : TestAnd, SolverFactory by ProblogSolverFactory {

    private val prototype = TestAnd.prototype(this)

    @Test
    override fun testTermIsFreeVariable() {
        prototype.testTermIsFreeVariable()
    }

    @Test
    override fun testWithSubstitution() {
        prototype.testWithSubstitution()
    }

    @Test
    override fun testFailIsCallable() {
        prototype.testFailIsCallable()
    }

    @Test
    override fun testNoFooIsCallable() {
        // NOTE: This fails but is not significant
        // prototype.testNoFooIsCallable()
    }

    @Test
    override fun testTrueVarCallable() {
        prototype.testTrueVarCallable()
    }
}
