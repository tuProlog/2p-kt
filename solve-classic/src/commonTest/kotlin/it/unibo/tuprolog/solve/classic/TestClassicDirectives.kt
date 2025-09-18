package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestDirectives
import kotlin.test.Test

class TestClassicDirectives :
    TestDirectives,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestDirectives.prototype(this)

    @Test
    override fun testDynamic1() {
        prototype.testDynamic1()
    }

    @Test
    override fun testStatic1() {
        prototype.testStatic1()
    }

    @Test
    override fun testInitialization1() {
        prototype.testInitialization1()
    }

    @Test
    override fun testSolve1() {
        prototype.testSolve1()
    }

    @Test
    override fun testWrongDirectives() {
        prototype.testWrongDirectives()
    }

    @Test
    override fun testSetFlag2() {
        prototype.testSetFlag2()
    }

    @Test
    override fun testSetPrologFlag2() {
        prototype.testSetPrologFlag2()
    }

    @Test
    override fun testOp3() {
        prototype.testOp3()
    }

    @Test
    override fun testFailingInitialization1() {
        prototype.testFailingInitialization1()
    }

    @Test
    override fun testFailingSolve1() {
        prototype.testFailingSolve1()
    }

    @Test
    override fun testExceptionalInitialization1() {
        prototype.testExceptionalInitialization1()
    }

    @Test
    override fun testExceptionalSolve1() {
        prototype.testExceptionalSolve1()
    }

    @Test
    override fun testDirectiveLoadingQuickly() {
        prototype.testDirectiveLoadingQuickly()
    }
}
