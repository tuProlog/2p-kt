package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestIs
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsIs : TestIs, SolverFactory by StreamsSolverFactory {
    private val prototype = TestIs.prototype(this)

    @Test
    override fun testIsResult() {
        prototype.testIsResult()
    }

    @Test
    @Ignore
    override fun testIsXY() {
        prototype.testIsXY()
    }

    @Test
    override fun testIsFoo() {
        prototype.testIsFoo()
    }

    @Test
    @Ignore
    override fun testIsNNumber() {
        prototype.testIsNNumber()
    }

    @Test
    @Ignore
    override fun testIsNumberFoo() {
        prototype.testIsNumberFoo()
    }

    @Test
    override fun testIsXFloat() {
        prototype.testIsXFloat()
    }
}
