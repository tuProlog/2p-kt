package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRepeat
import kotlin.test.Test

class TestClassicRepeat :
    TestRepeat,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestRepeat.prototype(this)

    @Test
    override fun testRepeat() {
        prototype.testRepeat()
    }
}
