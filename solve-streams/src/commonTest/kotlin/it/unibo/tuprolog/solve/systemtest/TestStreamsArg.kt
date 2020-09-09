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

class TestStreamsArg : TestArg, SolverFactory by StreamsSolverFactory {

    private val prototype = TestArg.prototype(this)

    @Test
    override fun testArgFromFoo() {
        prototype.testArgFromFoo()
    }

    @Test
    override fun testArgFromFooX() {
        prototype.testArgFromFooX()
    }

    @Test
    override fun testArgFromFoo2() {
        prototype.testArgFromFoo2()
    }

    @Test
    override fun testArgFromFooInF() {
        prototype.testArgFromFooInF()
    }

    @Test
    @Ignore
    override fun testArgFromFooY() {
        prototype.testArgFromFooY()
    }

    @Test
    override fun testArgFromFooInSecondTerm() {
        prototype.testArgFromFooInSecondTerm()
    }

    @Test
    override fun testArgFromFooInFoo() {
        prototype.testArgFromFooInFoo()
    }

    @Test
    override fun testArgNumberFromFoo() {
        prototype.testArgNumberFromFoo()
    }

    @Test
    override fun testArgXFromFoo() {
        prototype.testArgXFromFoo()
    }

    @Test
    @Ignore
    override fun testArgNumberFromX() {
        prototype.testArgNumberFromX()
    }

    @Test
    @Ignore
    override fun testArgFromAtom() {
        prototype.testArgFromAtom()
    }

    @Test
    @Ignore
    override fun testArgFromNumber() {
        prototype.testArgFromNumber()
    }

    @Test
    @Ignore
    override fun testNegativeArgFromFoo() {
        prototype.testNegativeArgFromFoo()
    }

    @Test
    @Ignore
    override fun testArgAFromFoo() {
        prototype.testArgAFromFoo()
    }
}