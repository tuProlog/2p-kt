package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCopyTerm
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCopyTerm :
    TestCopyTerm,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestCopyTerm.prototype(this)

    @Test
    override fun testCopyXNum() {
        prototype.testCopyXNum()
    }

    @Test
    override fun testCopyAnyA() {
        prototype.testCopyAnyA()
    }

    @Test
    override fun testCopySum() {
        prototype.testCopySum()
    }

    @Test
    override fun testCopyAnyAny() {
        prototype.testCopyAnyAny()
    }

    @Test
    override fun testCopyTripleSum() {
        prototype.testCopyTripleSum()
    }

    @Test
    override fun testCopyAA() {
        prototype.testCopyAA()
    }

    @Test
    override fun testCopyAB() {
        prototype.testCopyAB()
    }

    @Test
    override fun testCopyF() {
        prototype.testCopyF()
    }

    @Test
    override fun testDoubleCopy() {
        prototype.testDoubleCopy()
    }
}
