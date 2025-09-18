package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestArith
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsArith :
    TestArith,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestArith.prototype(this)

    @Test
    override fun testArithDiff() {
        prototype.testArithDiff()
    }

    @Test
    override fun testArithEq() {
        prototype.testArithEq()
    }

    @Test
    override fun testArithGreaterThan() {
        prototype.testArithGreaterThan()
    }

    @Test
    override fun testArithGreaterThanEq() {
        prototype.testArithGreaterThanEq()
    }

    @Test
    override fun testArithLessThan() {
        prototype.testArithLessThan()
    }

    @Test
    override fun testArithLessThanEq() {
        prototype.testArithLessThanEq()
    }
}
