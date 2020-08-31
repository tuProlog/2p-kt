package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCall
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicCall : TestCall, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestCall.prototype(this)

    @Test
    override fun testCallCut(){
        prototype.testCallCut()
    }

    @Test
    override fun testCallFail() {
        prototype.testCallFail()
    }

    @Test
    override fun testCallFailX() {
        prototype.testCallFailX()
    }

    @Test
    override fun testCallFailCall() {
        prototype.testCallFailCall()
    }

    @Test
    override fun testCallWriteX() {
        prototype.testCallWriteX()
    }

    @Test
    override fun testCallWriteCall() {
        prototype.testCallWriteCall()
    }

    @Test
    override fun testCallX() {
        prototype.testCallX()
    }

    @Test
    override fun testCallOne() {
        prototype.testCallOne()
    }

    @Test
    override fun testCallFailOne() {
        prototype.testCallFailOne()
    }

    @Test
    override fun testCallWriteOne() {
        prototype.testCallWriteOne()
    }

    @Test
    override fun testCallTrue() {
        prototype.testCallTrue()
    }
}