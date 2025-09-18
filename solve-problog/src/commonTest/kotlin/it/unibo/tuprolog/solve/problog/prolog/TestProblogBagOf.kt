package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBagOf
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogBagOf :
    TestBagOf,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestBagOf.prototype(this)

    @Test
    override fun testBagXInDifferentValues() {
        prototype.testBagXInDifferentValues()
    }

    @Test
    override fun testBagOfFindX() {
        prototype.testBagOfFindX()
    }

    @Test
    override fun testBagOfYXZ() {
        prototype.testBagOfYXZ()
    }

    @Test
    override fun testBagOfFail() {
        prototype.testBagOfFail()
    }

    @Test
    override fun testBagOfSameAsFindall() {
        prototype.testBagOfSameAsFindall()
    }

    @Test
    override fun testBagOfInstanceError() {
        prototype.testBagOfInstanceError()
    }

    @Test
    override fun testBagOfTypeError() {
        prototype.testBagOfTypeError()
    }
}
