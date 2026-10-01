package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTimeout
import kotlin.test.Test

class TestPrologTimeout :
    TestTimeout,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestTimeout.prototype(this)

    @Test
    override fun testSleep() {
        prototype.testSleep()
    }

    @Test
    override fun testInfiniteFindAll() {
        prototype.testInfiniteFindAll()
    }

    @Test
    override fun testInfiniteBagOf() {
        prototype.testInfiniteBagOf()
    }

    @Test
    override fun testInfiniteSetOf() {
        prototype.testInfiniteSetOf()
    }
}
