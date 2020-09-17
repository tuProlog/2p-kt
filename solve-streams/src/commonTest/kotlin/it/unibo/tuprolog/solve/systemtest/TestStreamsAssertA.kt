package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestAssertA
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAssertA : TestAssertA, SolverFactory by StreamsSolverFactory {

    private val prototype = TestAssertA.prototype(this)

    @Test
    override fun testAssertAClause() {
        prototype.testAssertAClause()
    }

    @Test
    @Ignore
    override fun testAssertAAny() {
        prototype.testAssertAAny()
    }

    @Test
    @Ignore
    override fun testAssertANumber() {
        prototype.testAssertANumber()
    }

    @Test
    @Ignore
    override fun testAssertAFooNumber() {
        prototype.testAssertAFooNumber()
    }

    @Test
    @Ignore
    override fun testAssertAAtomTrue() {
        prototype.testAssertAAtomTrue()
    }
}
