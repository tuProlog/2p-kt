package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAssertZ
import kotlin.test.Test

class TestClassicAssertZ :
    TestAssertZ,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestAssertZ.prototype(this)

    @Test
    override fun testAssertZClause() {
        prototype.testAssertZClause()
    }

    @Test
    override fun testAssertZAny() {
        prototype.testAssertZAny()
    }

    @Test
    override fun testAssertZNumber() {
        prototype.testAssertZNumber()
    }

    @Test
    override fun testAssertZFooNumber() {
        prototype.testAssertZFooNumber()
    }

    @Test
    override fun testAssertZAtomTrue() {
        prototype.testAssertZAtomTrue()
    }
}
