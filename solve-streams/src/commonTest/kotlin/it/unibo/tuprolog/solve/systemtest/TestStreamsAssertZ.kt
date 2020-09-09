package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAssertZ : TestAssertZ, SolverFactory by StreamsSolverFactory{

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