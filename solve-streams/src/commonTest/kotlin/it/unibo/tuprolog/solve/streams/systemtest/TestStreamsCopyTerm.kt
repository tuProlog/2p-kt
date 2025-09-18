package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCopyTerm
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCopyTerm :
    TestCopyTerm,
    SolverFactory by StreamsSolverFactory {
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
    override fun testCopyAnyAny() {
        prototype.testCopyAnyAny()
    }

    @Test
    @Ignore
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
