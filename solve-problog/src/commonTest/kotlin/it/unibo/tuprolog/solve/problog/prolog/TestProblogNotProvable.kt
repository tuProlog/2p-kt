package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNotProvable
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogNotProvable :
    TestNotProvable,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestNotProvable.prototype(this, Signature("ensure_executable", 1))

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
    override fun testOrNotCutFail() {
        prototype.testOrNotCutFail()
    }
}
