package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCall
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCall :
    TestCall,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestCall.prototype(this, Signature("ensure_executable", 1))

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
