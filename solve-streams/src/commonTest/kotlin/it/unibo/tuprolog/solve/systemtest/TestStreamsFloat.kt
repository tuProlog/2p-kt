package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestFloat
import kotlin.test.Test

class TestStreamsFloat : TestFloat, SolverFactory by StreamsSolverFactory {
    private val prototype = TestFloat.prototype(this)

    @Test
    override fun testFloatDec() {
        prototype.testFloatDec()
    }

    @Test
    override fun testFloatDecNeg() {
        prototype.testFloatDecNeg()
    }

    @Test
    override fun testFloatNat() {
        prototype.testFloatNat()
    }

    @Test
    override fun testFloatAtom() {
        prototype.testFloatAtom()
    }

    @Test
    override fun testFloatX() {
        prototype.testFloatX()
    }
}
