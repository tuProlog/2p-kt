package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOnce
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsOnce :
    TestOnce,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestOnce.prototype(this)

    override val errorSignature: Signature
        get() = prototype.errorSignature

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
    @Ignore
    override fun testOnceNum() {
        prototype.testOnceNum()
    }

    @Test
    @Ignore
    override fun testOnceX() {
        prototype.testOnceX()
    }
}
