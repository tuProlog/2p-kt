package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCompound
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCompound :
    TestCompound,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestCompound.prototype(this)

    @Test
    override fun testCompoundDec() {
        prototype.testCompoundDec()
    }

    @Test
    override fun testCompoundNegDec() {
        prototype.testCompoundNegDec()
    }

    @Test
    override fun testCompoundNegA() {
        prototype.testCompoundNegA()
    }

    @Test
    override fun testCompoundAny() {
        prototype.testCompoundAny()
    }

    @Test
    override fun testCompoundA() {
        prototype.testCompoundA()
    }

    @Test
    override fun testCompoundAOfB() {
        prototype.testCompoundAOfB()
    }

    @Test
    override fun testCompoundListA() {
        prototype.testCompoundListA()
    }
}
