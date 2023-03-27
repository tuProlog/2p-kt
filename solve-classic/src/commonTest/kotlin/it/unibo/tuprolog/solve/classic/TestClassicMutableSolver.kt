package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestMutableSolver
import kotlin.test.Test

class TestClassicMutableSolver : TestMutableSolver, SolverFactory by ClassicSolverFactory {
    private val prototype = TestMutableSolver.prototype(this)

    @Test
    override fun testSetStdIn() {
        prototype.testSetStdIn()
    }

    @Test
    override fun testSetStdOut() {
        prototype.testSetStdOut()
    }

    @Test
    override fun testSetStdErr() {
        prototype.testSetStdErr()
    }

    @Test
    override fun testSetStdWarn() {
        prototype.testSetStdWarn()
    }
}
