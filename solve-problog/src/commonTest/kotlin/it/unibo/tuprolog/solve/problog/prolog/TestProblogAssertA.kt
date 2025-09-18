package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAssertA
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestProblogAssertA :
    TestAssertA,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAssertA.prototype(this)

    // NOTE: Ignored because it relies on the internal representation of the knowledge base, not significant here
    @Ignore
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
