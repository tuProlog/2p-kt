package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAnd
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestProblogAnd :
    TestAnd,
    SolverFactory by ProblogSolverFactory {
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

    // NOTE: Ignored because it relies on the internal representation of the knowledge base, not significant here
    @Ignore
    @Test
    override fun testNoFooIsCallable() {
        prototype.testNoFooIsCallable()
    }

    @Test
    override fun testTrueVarCallable() {
        prototype.testTrueVarCallable()
    }
}
