package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestIfThenElse
import kotlin.test.Test

class TestStreamsIfThenElse : TestIfThenElse, SolverFactory by StreamsSolverFactory {
    private val prototype = TestIfThenElse.prototype(this)

    @Test
    override fun testIfTrueElseFail() {
        prototype.testIfTrueElseFail()
    }

    @Test
    override fun testIfFailElseTrue() {
        prototype.testIfFailElseTrue()
    }

    @Test
    override fun testIfTrueThenElseFail() {
        prototype.testIfTrueThenElseFail()
    }

    @Test
    override fun testIfFailElseFail() {
        prototype.testIfFailElseFail()
    }

    @Test
    override fun testIfXTrueElseX() {
        prototype.testIfXTrueElseX()
    }

    @Test
    override fun testIfFailElseX() {
        prototype.testIfFailElseX()
    }

    @Test
    override fun testIfThenElseOrWithDoubleSub() {
        prototype.testIfThenElseOrWithDoubleSub()
    }

    @Test
    override fun testIfOrElseTrue() {
        prototype.testIfOrElseTrue()
    }
}