package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRetract
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicRetract : TestRetract, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestRetract.prototype(this)

    @Test
    @Ignore
    override fun testRetractNumIfX() {
        prototype.testRetractNumIfX()
    }

    @Test
    @Ignore
    override fun testRetractAtomEmptyList() {
        prototype.testRetractAtomEmptyList()
    }
}