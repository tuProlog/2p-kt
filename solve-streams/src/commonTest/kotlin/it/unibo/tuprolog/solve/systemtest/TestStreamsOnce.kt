package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOnce
import kotlin.test.Test

class TestStreamsOnce : TestOnce, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestOnce.prototype(this)

    @Test
    override fun testOnceCut() {
        prototype.testOnceCut()
    }

    @Test
    override fun testOnceCutOr() {
        prototype.testOnceCutOr()
    }

    @Test
    override fun testOnceRepeat() {
        prototype.testOnceRepeat()
    }

    @Test
    override fun testOnceFail() {
        prototype.testOnceFail()
    }

    @Test
    override fun testOnceNum() {
        prototype.testOnceNum()
    }

    @Test
    override fun testOnceX() {
        prototype.testOnceX()
    }
}