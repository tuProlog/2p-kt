package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestIs
import kotlin.test.Test

class TestClassicIs :
    TestIs,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestIs.prototype(this)

    @Test
    override fun testIsResult() {
        prototype.testIsResult()
    }

    @Test
    override fun testIsXY() {
        prototype.testIsXY()
    }

    @Test
    override fun testIsFoo() {
        prototype.testIsFoo()
    }

    @Test
    override fun testIsNNumber() {
        prototype.testIsNNumber()
    }

    @Test
    override fun testIsNumberFoo() {
        prototype.testIsNumberFoo()
    }

    @Test
    override fun testIsXFloat() {
        prototype.testIsXFloat()
    }
}
