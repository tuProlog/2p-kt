package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCopyTerm
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicCopyTerm  : TestCopyTerm, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestCopyTerm.prototype(this)

    @Test
    @Ignore
    override fun testCopyXNum() {
        prototype.testCopyXNum()
    }

    @Test
    @Ignore
    override fun testCopyAnyA() {
        prototype.testCopyAnyA()
    }

    @Test
    @Ignore
    override fun testCopySum() {
        prototype.testCopySum()
    }

    @Test
    @Ignore
    override fun testCopyAnyAny() {
        prototype.testCopyAnyAny()
    }

    @Test
    @Ignore
    override fun testCopyTripleSum() {
        prototype.testCopyTripleSum()
    }

    @Test
    @Ignore
    override fun testCopyAA() {
        prototype.testCopyAA()
    }

    @Test
    @Ignore
    override fun testCopyAB() {
        prototype.testCopyAB()
    }

    @Test
    @Ignore
    override fun testCopyF() {
        prototype.testCopyF()
    }

    @Test
    @Ignore
    override fun testDoubleCopy() {
        prototype.testDoubleCopy()
    }
}