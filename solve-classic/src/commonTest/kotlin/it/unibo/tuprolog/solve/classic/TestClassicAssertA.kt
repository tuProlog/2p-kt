package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAssertA
import kotlin.test.Test

class TestClassicAssertA :
    TestAssertA,
    SolverFactory by ClassicSolverFactory {
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
