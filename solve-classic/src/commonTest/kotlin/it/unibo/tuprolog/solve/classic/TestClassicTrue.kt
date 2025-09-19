package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTrue
import kotlin.test.Test

class TestClassicTrue :
    TestTrue,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestTrue.prototype(this)

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }
}
