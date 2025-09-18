package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFloat
import kotlin.test.Test

class TestClassicFloat :
    TestFloat,
    SolverFactory by ClassicSolverFactory {
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
