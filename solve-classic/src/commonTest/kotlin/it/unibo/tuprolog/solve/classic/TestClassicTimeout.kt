package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTimeout
import kotlin.test.Test

class TestClassicTimeout :
    TestTimeout,
    SolverFactory by ClassicSolverFactory {
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
