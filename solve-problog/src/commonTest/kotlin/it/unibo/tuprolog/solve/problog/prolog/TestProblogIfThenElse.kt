package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestIfThenElse
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogIfThenElse :
    TestIfThenElse,
    SolverFactory by ProblogSolverFactory {
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
