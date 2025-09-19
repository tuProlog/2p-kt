package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSetOf
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsSetOf :
    TestSetOf,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestSetOf.prototype(this)

    @Test
    override fun testSetOfBasic() {
        prototype.testSetOfBasic()
    }

    @Test
    override fun testSetOfX() {
        prototype.testSetOfX()
    }

    @Test
    override fun testSetOfSorted() {
        prototype.testSetOfSorted()
    }

    @Test
    override fun testSetOfDoubled() {
        prototype.testSetOfDoubled()
    }

    @Test
    @Ignore
    override fun testSetOfFail() {
        prototype.testSetOfFail()
    }

    @Test
    @Ignore
    override fun testSetOfAsFindAll() {
        prototype.testSetOfAsFindAll()
    }
}
