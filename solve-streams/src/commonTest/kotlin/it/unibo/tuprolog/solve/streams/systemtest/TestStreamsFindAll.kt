package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFindAll
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsFindAll :
    TestFindAll,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestFindAll.prototype(this)

    override val errorSignature: Signature
        get() = prototype.errorSignature

    @Test
    override fun testFindXInDiffValues() {
        prototype.testFindXInDiffValues()
    }

    @Test
    override fun testFindSumResult() {
        prototype.testFindSumResult()
    }

    @Test
    override fun testFindXinFail() {
        prototype.testFindXinFail()
    }

    @Test
    override fun testFindXinSameXValues() {
        prototype.testFindXinSameXValues()
    }

    @Test
    override fun testResultListIsCorrect() {
        prototype.testResultListIsCorrect()
    }

    @Test
    override fun testFindXtoDoubleAssigment() {
        prototype.testFindXtoDoubleAssigment()
    }

    @Test
    @Ignore
    override fun testFindXinGoal() {
        prototype.testFindXinGoal()
    }

    @Test
    @Ignore
    override fun testFindXinNumber() {
        prototype.testFindXinNumber()
    }

    @Test
    @Ignore
    override fun testFindXinCall() {
        prototype.testFindXinCall()
    }
}
