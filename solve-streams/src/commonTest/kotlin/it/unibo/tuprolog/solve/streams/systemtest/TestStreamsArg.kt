package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestArg
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsArg :
    TestArg,
    SolverFactory by StreamsSolverFactory {
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
