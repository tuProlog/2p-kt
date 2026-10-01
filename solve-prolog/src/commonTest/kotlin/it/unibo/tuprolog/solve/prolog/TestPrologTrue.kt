package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTrue
import kotlin.test.Test

class TestPrologTrue :
    TestTrue,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestTrue.prototype(this)

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }
}
