package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestNotProvable
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsNotProvable : TestNotProvable, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestNotProvable.prototype(this)

    @Test
    override fun testNPTrue() {
        prototype.testNPTrue()
    }

    @Test
    override fun testNPCut() {
        prototype.testNPCut()
    }

    @Test
    @Ignore //solver returns no
    override fun testNPCutFail() {
        prototype.testNPCutFail()
    }

    @Test
    override fun testNPEquals() {
        prototype.testNPEquals()
    }

    @Test
    override fun testNPNum() {
        prototype.testNPNum()
    }

    @Test
    override fun testNPX() {
        prototype.testNPX()
    }

    @Test
    @Ignore //solver returns no
    override fun testOrNotCutFail() {
        prototype.testOrNotCutFail()
    }
}