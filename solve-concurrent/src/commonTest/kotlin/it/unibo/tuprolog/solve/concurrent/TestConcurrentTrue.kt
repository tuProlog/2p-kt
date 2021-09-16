package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTrue
import kotlin.test.Test

class TestConcurrentTrue : TestTrue, SolverFactory by ConcurrentSolverFactory {
    private val prototype = TestTrue.prototype(this)

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }
}