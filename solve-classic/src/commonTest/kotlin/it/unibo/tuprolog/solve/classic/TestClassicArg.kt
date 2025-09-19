package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestArg
import kotlin.test.Test

class TestClassicArg :
    TestArg,
    SolverFactory by ClassicSolverFactory {
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
    override fun testArgNumberFromX() {
        prototype.testArgNumberFromX()
    }

    @Test
    override fun testArgFromAtom() {
        prototype.testArgFromAtom()
    }

    @Test
    override fun testArgFromNumber() {
        prototype.testArgFromNumber()
    }

    @Test
    override fun testNegativeArgFromFoo() {
        prototype.testNegativeArgFromFoo()
    }

    @Test
    override fun testArgAFromFoo() {
        prototype.testArgAFromFoo()
    }
}
