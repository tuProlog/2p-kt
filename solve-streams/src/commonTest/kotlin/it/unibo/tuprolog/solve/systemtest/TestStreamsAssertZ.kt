package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAssertZ : TestAssertZ, SolverFactory by StreamsSolverFactory {

    private val prototype = TestAssertZ.prototype(this)

    @Test
    override fun testAssertZClause() {
        prototype.testAssertZClause()
    }

    @Test
    @Ignore
    override fun testAssertZAny() {
        prototype.testAssertZAny()
    }

    @Test
    @Ignore
    override fun testAssertZNumber() {
        prototype.testAssertZNumber()
    }

    @Test
    @Ignore
    override fun testAssertZFooNumber() {
        prototype.testAssertZFooNumber()
    }

    @Test
    @Ignore
    override fun testAssertZAtomTrue() {
        prototype.testAssertZAtomTrue()
    }
}
