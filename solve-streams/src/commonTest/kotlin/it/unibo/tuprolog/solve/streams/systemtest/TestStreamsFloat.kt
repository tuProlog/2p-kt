package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFloat
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsFloat :
    TestFloat,
    SolverFactory by StreamsSolverFactory {
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
