package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAnd
import kotlin.test.Test

class TestClassicAnd :
    TestAnd,
    SolverFactory by ClassicSolverFactory {
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
        prototype.testNoFooIsCallable()
    }

    @Test
    override fun testTrueVarCallable() {
        prototype.testTrueVarCallable()
    }
}
