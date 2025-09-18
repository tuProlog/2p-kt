package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOnce
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogOnce :
    TestOnce,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestOnce.prototype(this, Signature("ensure_executable", 1))

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
    override fun testOnceNum() {
        prototype.testOnceNum()
    }

    @Test
    override fun testOnceX() {
        prototype.testOnceX()
    }
}
