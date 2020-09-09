package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.Ignore

class TestClassicAssertA : TestAssertA, SolverFactory by ClassicSolverFactory{
    private val prototype = TestAssertA.prototype(this)

    @Test
    override fun testAssertAClause() {
        prototype.testAssertAClause()
    }

    @Test
    override fun testAssertAAny() {
        prototype.testAssertAAny()
    }

    @Test
    override fun testAssertANumber() {
        prototype.testAssertANumber()
    }

    @Test
    override fun testAssertAFooNumber() {
        prototype.testAssertAFooNumber()
    }

    @Test
    override fun testAssertAAtomTrue() {
        prototype.testAssertAAtomTrue()
    }
}