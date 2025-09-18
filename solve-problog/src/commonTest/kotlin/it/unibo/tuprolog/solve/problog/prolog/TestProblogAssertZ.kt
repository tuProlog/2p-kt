package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAssertZ
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAssertZ :
    TestAssertZ,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAssertZ.prototype(this)

    @Test
    override fun testAssertZClause() {
        prototype.testAssertZClause()
    }

    @Test
    override fun testAssertZAny() {
        prototype.testAssertZAny()
    }

    @Test
    override fun testAssertZNumber() {
        prototype.testAssertZNumber()
    }

    @Test
    override fun testAssertZFooNumber() {
        prototype.testAssertZFooNumber()
    }

    @Test
    override fun testAssertZAtomTrue() {
        prototype.testAssertZAtomTrue()
    }
}
