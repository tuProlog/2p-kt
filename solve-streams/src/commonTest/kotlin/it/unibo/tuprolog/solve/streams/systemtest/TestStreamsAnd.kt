package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAnd
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAnd :
    TestAnd,
    SolverFactory by StreamsSolverFactory {
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
    @Ignore
    override fun testNoFooIsCallable() {
        prototype.testNoFooIsCallable()
    }

    @Test
    override fun testTrueVarCallable() {
        prototype.testTrueVarCallable()
    }
}
