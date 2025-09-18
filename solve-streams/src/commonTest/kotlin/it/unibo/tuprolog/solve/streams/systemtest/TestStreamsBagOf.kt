package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBagOf
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsBagOf :
    TestBagOf,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestBagOf.prototype(this)

    @Test
    @Ignore
    override fun testBagXInDifferentValues() {
        prototype.testBagXInDifferentValues()
    }

    @Test
    override fun testBagOfFindX() {
        prototype.testBagOfFindX()
    }

    @Test
    @Ignore
    override fun testBagOfYXZ() {
        prototype.testBagOfYXZ()
    }

    @Test
    @Ignore
    override fun testBagOfFail() {
        prototype.testBagOfFail()
    }

    @Test
    override fun testBagOfSameAsFindall() {
        prototype.testBagOfSameAsFindall()
    }

    @Test
    @Ignore
    override fun testBagOfInstanceError() {
        prototype.testBagOfInstanceError()
    }

    @Test
    @Ignore
    override fun testBagOfTypeError() {
        prototype.testBagOfTypeError()
    }
}
