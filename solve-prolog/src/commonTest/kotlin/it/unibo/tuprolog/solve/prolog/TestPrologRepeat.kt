package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRepeat
import kotlin.test.Test

class TestPrologRepeat :
    TestRepeat,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestRepeat.prototype(this)

    @Test
    override fun testRepeat() {
        prototype.testRepeat()
    }
}
