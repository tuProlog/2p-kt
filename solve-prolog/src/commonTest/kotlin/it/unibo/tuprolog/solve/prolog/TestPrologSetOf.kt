package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSetOf
import kotlin.test.Test

class TestPrologSetOf :
    TestSetOf,
    SolverFactory by PrologSolverFactory {
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
    override fun testSetOfFail() {
        prototype.testSetOfFail()
    }

    @Test
    override fun testSetOfAsFindAll() {
        prototype.testSetOfAsFindAll()
    }
}
