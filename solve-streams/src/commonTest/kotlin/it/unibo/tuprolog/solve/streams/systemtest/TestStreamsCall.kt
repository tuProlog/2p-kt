package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCall
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCall :
    TestCall,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestCall.prototype(this)

    override val errorSignature: Signature
        get() = prototype.errorSignature

    @Test
    override fun testCallCut() {
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
    @Ignore
    override fun testCallWriteX() {
        prototype.testCallWriteX()
    }

    @Test
    @Ignore
    override fun testCallWriteCall() {
        prototype.testCallWriteCall()
    }

    @Test
    @Ignore
    override fun testCallX() {
        prototype.testCallX()
    }

    @Test
    @Ignore
    override fun testCallOne() {
        prototype.testCallOne()
    }

    @Test
    @Ignore
    override fun testCallFailOne() {
        prototype.testCallFailOne()
    }

    @Test
    @Ignore
    override fun testCallWriteOne() {
        prototype.testCallWriteOne()
    }

    @Test
    @Ignore
    override fun testCallTrue() {
        prototype.testCallTrue()
    }
}
