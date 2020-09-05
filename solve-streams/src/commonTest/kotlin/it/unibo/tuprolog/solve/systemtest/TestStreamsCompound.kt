package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestCompound
import kotlin.test.Test

class TestStreamsCompound : TestCompound, SolverFactory by StreamsSolverFactory  {
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