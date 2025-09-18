package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAssertA
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAssertA :
    TestAssertA,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestAssertA.prototype(this)

    @Test
    @Ignore // fails on windows with JDK openj9
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
