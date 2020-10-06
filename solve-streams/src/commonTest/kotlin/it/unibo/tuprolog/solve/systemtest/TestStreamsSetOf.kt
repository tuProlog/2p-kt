package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestSetOf
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsSetOf : TestSetOf, SolverFactory by StreamsSolverFactory {
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
    override fun testSetOfNoSorted() {
        prototype.testSetOfNoSorted()
    }

    @Test
    override fun testSetOfDoubled() {
        prototype.testSetOfDoubled()
    }

    @Test
    override fun testSetOfFail() {
        prototype.testSetOfFail()
    }

    @Test
    @Ignore
    override fun testSetOfAsFindAll() {
        prototype.testSetOfAsFindAll()
    }
}
