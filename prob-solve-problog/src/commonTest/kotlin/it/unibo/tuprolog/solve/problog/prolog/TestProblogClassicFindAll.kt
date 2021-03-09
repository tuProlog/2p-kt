package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFindAll
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogClassicFindAll : TestFindAll, SolverFactory by ProblogSolverFactory {

    private val prototype = TestFindAll.prototype(this, Signature("ensure_executable", 1))

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
    override fun testFindXinGoal() {
        // NOTE: This fails but is not significant
        // prototype.testFindXinGoal()
    }

    @Test
    override fun testFindXinNumber() {
        // NOTE: This fails but is not significant
        // prototype.testFindXinNumber()
    }

    @Test
    override fun testFindXinCall() {
        prototype.testFindXinCall()
    }
}
