package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRetract
import kotlin.test.Test

class TestPrologRetract :
    TestRetract,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestRetract.prototype(this)

    @Test
    override fun testRetractNumIfX() {
        prototype.testRetractNumIfX()
    }

    @Test
    override fun testRetractAtomEmptyList() {
        prototype.testRetractAtomEmptyList()
    }
}
