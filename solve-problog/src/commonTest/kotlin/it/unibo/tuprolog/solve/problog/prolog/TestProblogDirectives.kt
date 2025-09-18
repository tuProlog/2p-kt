package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestDirectives
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

/* NOTE: Many of these tests are ignored because they heavily rely on the representation of static and dynamic
Knowledge Base, which is altered in this implementation due to internal mappings. */
class TestProblogDirectives :
    TestDirectives,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestDirectives.prototype(this)

    @Ignore
    @Test
    override fun testDynamic1() {
        prototype.testDynamic1()
    }

    @Ignore
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

    @Ignore
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

    @Ignore
    @Test
    override fun testFailingInitialization1() {
        prototype.testFailingInitialization1()
    }

    @Ignore
    @Test
    override fun testFailingSolve1() {
        prototype.testFailingSolve1()
    }

    @Ignore
    @Test
    override fun testExceptionalInitialization1() {
        prototype.testExceptionalInitialization1()
    }

    @Ignore
    @Test
    override fun testExceptionalSolve1() {
        prototype.testExceptionalSolve1()
    }

    @Ignore
    @Test
    override fun testDirectiveLoadingQuickly() {
        prototype.testDirectiveLoadingQuickly()
    }
}
