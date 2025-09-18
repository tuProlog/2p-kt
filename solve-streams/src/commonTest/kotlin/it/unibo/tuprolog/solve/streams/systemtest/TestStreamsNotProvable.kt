package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNotProvable
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsNotProvable :
    TestNotProvable,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestNotProvable.prototype(this)

    override val errorSignature: Signature
        get() = prototype.errorSignature

    @Test
    override fun testNPTrue() {
        prototype.testNPTrue()
    }

    @Test
    override fun testNPCut() {
        prototype.testNPCut()
    }

    @Test
    @Ignore // solver returns no
    override fun testNPCutFail() {
        prototype.testNPCutFail()
    }

    @Test
    override fun testNPEquals() {
        prototype.testNPEquals()
    }

    @Test
    @Ignore
    override fun testNPNum() {
        prototype.testNPNum()
    }

    @Test
    @Ignore
    override fun testNPX() {
        prototype.testNPX()
    }

    @Test
    @Ignore // solver returns no
    override fun testOrNotCutFail() {
        prototype.testOrNotCutFail()
    }
}
