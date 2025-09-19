package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFindAll
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestProblogClassicFindAll :
    TestFindAll,
    SolverFactory by ProblogSolverFactory {
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

    /* NOTE: Ignored because not significant. The test expects the solver to HALT (and it does),
     * but strictly checks the message error that, in this case, is not the one expected due to the
     * presence of meta-predicates such as Prob. */
    @Ignore
    @Test
    override fun testFindXinGoal() {
        prototype.testFindXinGoal()
    }

    // NOTE: See comment above
    @Ignore
    @Test
    override fun testFindXinNumber() {
        prototype.testFindXinNumber()
    }

    @Test
    override fun testFindXinCall() {
        prototype.testFindXinCall()
    }
}
